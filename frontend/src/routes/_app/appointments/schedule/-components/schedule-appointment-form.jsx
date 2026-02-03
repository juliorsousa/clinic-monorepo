import SpecialtyPicker from "@/components/specialty-picker";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
	Form,
	FormControl,
	FormField,
	FormItem,
	FormLabel,
	FormMessage,
} from "@/components/ui/form";
import {
	Select,
	SelectContent,
	SelectItem,
	SelectTrigger,
	SelectValue,
} from "@/components/ui/select";
import { api } from "@/lib/api";
import {
	useDoctorsAvailabilityDates,
	useDoctorsAvailabilityHours,
} from "@/lib/hooks/use-doctors-availability";
import { useSummarizedDoctorsBySpecialty } from "@/lib/hooks/use-summarized-doctor";
import { formatDate, formatDateToHour } from "@/utils/format-date";
import { zodResolver } from "@hookform/resolvers/zod";
import { useNavigate } from "@tanstack/react-router";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import z from "zod";

const appointmentSchema = z.object({
	specialty: z.string().min(1, "Escolha a especialidade."),
	doctorId: z.string().optional(),
	date: z.string().min(1, "Escolha a data."),
	time: z.string().min(1, "Escolha o horário."),
});

const ANY_DOCTOR = "__ANY_DOCTOR__";

export default function ScheduleAppointmentForm() {
	const navigate = useNavigate();

	const form = useForm({
		mode: "onChange",
		resolver: zodResolver(appointmentSchema),
		defaultValues: {
			specialty: "",
			doctorId: "",
			date: "",
			time: "",
		},
	});

	const specialty = form.watch("specialty");
	const doctorId = form.watch("doctorId");
	const date = form.watch("date");

	const { data: doctors = [], isLoading: isDoctorsLoading } =
		useSummarizedDoctorsBySpecialty(specialty || undefined);

	const doctorIdsForQuery = !specialty
		? null
		: doctorId
			? [doctorId]
			: doctors.map((d) => d.id);

	const {
		data: availableDates = [],
		isLoading: isDatesLoading,
		isError: isDatesError,
	} = useDoctorsAvailabilityDates(doctorIdsForQuery);

	const shouldFetchHours =
		Boolean(date) &&
		Array.isArray(doctorIdsForQuery) &&
		doctorIdsForQuery.length > 0;

	const {
		data: rawAvailableTimes = {},
		isLoading: isTimesLoading,
		isError: isTimesError,
	} = useDoctorsAvailabilityHours(
		shouldFetchHours ? date : null,
		shouldFetchHours ? doctorIdsForQuery : null,
	);

	const availableTimes = Object.values(rawAvailableTimes ?? {}).flat();

	const resetAfterSpecialtyChange = (value) => {
		form.setValue("specialty", value, { shouldValidate: true });
		form.setValue("doctorId", "");
		form.setValue("date", "");
		form.setValue("time", "");
	};

	const resetAfterDoctorChange = (value) => {
		form.setValue("doctorId", value === ANY_DOCTOR ? "" : value, {
			shouldValidate: true,
		});
		form.setValue("date", "");
		form.setValue("time", "");
	};

	async function onSubmit(values) {
		try {
			const response = await api.post("/appointments/", {
				specialty: values.specialty,
				doctorId: values.doctorId || null,
				dateTime: values.time,
			});

			if (response.status !== 201) {
				throw new Error("Não foi possível agendar a consulta.");
			}

			const doctorAssignedId = response.data.doctorId;
			const doctorAssigned = doctors.find((d) => d.id === doctorAssignedId);

			const doctorName = doctorAssignedId
				? `o Dr(a). ${doctorAssigned?.name}`
				: "qualquer médico disponível";

			toast.success(
				`Consulta agendada para ${formatDate(
					new Date(values.time),
				)} com ${doctorName}.`,
			);

			navigate({ to: "/patients/me/appointments" });
		} catch (error) {
			console.error("Erro ao agendar consulta:", error);

			const message = error.response?.data?.message || error.message;
			toast.error(`Erro ao agendar consulta: ${message}`);
		}
	}

	return (
		<Form {...form}>
			<form onSubmit={form.handleSubmit(onSubmit)}>
				<Card className="max-w-3xl mx-auto">
					<CardHeader className="border-b">
						<CardTitle className="text-xl">Agendar Consulta</CardTitle>
					</CardHeader>

					<CardContent className="p-6">
						<div className="grid grid-cols-1 gap-6">
							<FormField
								control={form.control}
								name="specialty"
								render={({ field }) => (
									<FormItem>
										<FormLabel>Especialidade</FormLabel>
										<FormControl>
											<SpecialtyPicker
												value={field.value}
												onChange={resetAfterSpecialtyChange}
											/>
										</FormControl>
										<FormMessage />
									</FormItem>
								)}
							/>

							<div className="flex flex-row gap-4">
								<FormField
									control={form.control}
									name="doctorId"
									render={({ field }) => (
										<FormItem>
											<FormLabel>Médico</FormLabel>
											<FormControl>
												<Select
													value={field.value || ANY_DOCTOR}
													onValueChange={resetAfterDoctorChange}
													disabled={!specialty}
												>
													<SelectTrigger className="w-83">
														<SelectValue placeholder="Qualquer médico disponível" />
													</SelectTrigger>

													<SelectContent>
														<SelectItem value={ANY_DOCTOR}>
															Qualquer médico disponível
														</SelectItem>

														{isDoctorsLoading ? (
															<SelectItem value="loading" disabled>
																Carregando médicos...
															</SelectItem>
														) : (
															doctors.map((d) => (
																<SelectItem key={d.id} value={d.id}>
																	Dr(a). {d.name}
																</SelectItem>
															))
														)}
													</SelectContent>
												</Select>
											</FormControl>
											<FormMessage />
										</FormItem>
									)}
								/>

								{/* DATA - largura fixa */}
								<FormField
									control={form.control}
									name="date"
									render={({ field }) => (
										<FormItem>
											<FormLabel>Data</FormLabel>
											<FormControl>
												<Select
													value={field.value}
													onValueChange={(v) => {
														form.setValue("date", v, { shouldValidate: true });
														form.setValue("time", "");
													}}
													disabled={
														!specialty ||
														isDatesLoading ||
														isDatesError ||
														availableDates.length === 0
													}
												>
													<SelectTrigger className="w-42">
														<SelectValue placeholder="Selecione a data" />
													</SelectTrigger>

													<SelectContent>
														{isDatesLoading && (
															<SelectItem value="loading" disabled>
																Buscando datas...
															</SelectItem>
														)}

														{isDatesError && (
															<SelectItem value="error" disabled>
																Erro ao carregar datas.
															</SelectItem>
														)}

														{!isDatesLoading && availableDates.length === 0 && (
															<SelectItem value="empty" disabled>
																Nenhuma data disponível
															</SelectItem>
														)}

														{availableDates.map((d) => (
															<SelectItem key={d} value={d}>
																{d.split("-").reverse().join("/")}
															</SelectItem>
														))}
													</SelectContent>
												</Select>
											</FormControl>
											<FormMessage />
										</FormItem>
									)}
								/>

								{/* HORÁRIO - largura fixa */}
								<FormField
									control={form.control}
									name="time"
									render={({ field }) => (
										<FormItem>
											<FormLabel>Horário</FormLabel>
											<FormControl>
												<Select
													value={field.value}
													onValueChange={(v) =>
														form.setValue("time", v, { shouldValidate: true })
													}
													disabled={
														!date ||
														isTimesLoading ||
														isTimesError ||
														availableTimes.length === 0
													}
												>
													<SelectTrigger className="w-47">
														<SelectValue placeholder="Selecione o horário" />
													</SelectTrigger>

													<SelectContent>
														{isTimesLoading && (
															<SelectItem value="loading" disabled>
																Buscando horários...
															</SelectItem>
														)}

														{isTimesError && (
															<SelectItem value="error" disabled>
																Erro ao carregar horários.
															</SelectItem>
														)}

														{!isTimesLoading && availableTimes.length === 0 && (
															<SelectItem value="empty" disabled>
																Nenhum horário disponível
															</SelectItem>
														)}

														{availableTimes.map((t) => (
															<SelectItem key={t} value={t}>
																{formatDateToHour(new Date(t))}
															</SelectItem>
														))}
													</SelectContent>
												</Select>
											</FormControl>
											<FormMessage />
										</FormItem>
									)}
								/>
							</div>
						</div>

						<div className="mt-8">
							<Button
								type="submit"
								disabled={!form.formState.isValid}
								className="w-full"
							>
								Confirmar agendamento
							</Button>
						</div>
					</CardContent>
				</Card>
			</form>
		</Form>
	);
}
