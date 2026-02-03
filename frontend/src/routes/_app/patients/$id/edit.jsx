import CityPicker from "@/components/brazil-city-picker";
import StatePicker from "@/components/brazil-state-picker";
import { Loading } from "@/components/loading";
import {
	Breadcrumb,
	BreadcrumbItem,
	BreadcrumbLink,
	BreadcrumbList,
	BreadcrumbPage,
	BreadcrumbSeparator,
} from "@/components/ui/breadcrumb";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import {
	Form,
	FormControl,
	FormField,
	FormItem,
	FormLabel,
	FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Separator } from "@/components/ui/separator";
import { SidebarTrigger } from "@/components/ui/sidebar";
import { useAuth } from "@/hooks/use-auth";
import { api } from "@/lib/api";
import { usePatient } from "@/lib/hooks/use-patient";
import { queryClient } from "@/lib/query-client";
import { maskPostalCode, unmaskPostalCode } from "@/utils/cep-utils";
import { maskCPF, unmaskCPF, validateCPF } from "@/utils/cpf-utils";
import { maskPhone, unmaskPhone } from "@/utils/phone-utils";
import { zodResolver } from "@hookform/resolvers/zod";
import { Link, createFileRoute } from "@tanstack/react-router";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import z from "zod";

export const Route = createFileRoute("/_app/patients/$id/edit")({
	component: EditPatientPage,
});

const patientFormSchema = z.object({
	fullName: z
		.string()
		.min(1, "O nome completo é obrigatório.")
		.max(100, "O nome completo deve ter no máximo 100 caracteres."),
	document: z
		.string()
		.min(1, "O documento é obrigatório.")
		.max(20, "O documento deve ter no máximo 20 caracteres.")
		.refine((value) => validateCPF(value), "CPF inválido."),
	phone: z
		.string()
		.regex(/^\d{2}9?\d{8}$/, "Telefone inválido.")
		.min(1, "O telefone é obrigatório.")
		.max(15, "O telefone deve ter no máximo 15 caracteres."),
	street: z
		.string()
		.min(1, "A rua é obrigatória.")
		.max(100, "A rua deve ter no máximo 100 caracteres."),
	house: z
		.string()
		.min(1, "O número da casa é obrigatório.")
		.max(10, "O número da casa deve ter no máximo 10 caracteres."),
	complement: z
		.string()
		.max(50, "O complemento deve ter no máximo 50 caracteres."),
	neighborhood: z
		.string()
		.min(1, "O bairro é obrigatório.")
		.max(50, "O bairro deve ter no máximo 50 caracteres."),
	city: z
		.string()
		.min(1, "A cidade é obrigatória.")
		.max(50, "A cidade deve ter no máximo 50 caracteres."),
	state: z.string().min(1, "O estado é obrigatório."),
	zipCode: z
		.string()
		.min(1, "O CEP é obrigatório.")
		.regex(/^\d{8}$/, "CEP inválido."),
});

function EditPatientPage() {
	const { getSpecificRoleId } = useAuth();
	const { id: pathId } = Route.useParams();

	const specificRoleId = getSpecificRoleId("PATIENT");

	const id = pathId === "me" && specificRoleId ? specificRoleId : pathId;

	const { data: patient, isLoading } = usePatient(id);

	const form = useForm({
		resolver: zodResolver(patientFormSchema),
		defaultValues: {
			fullName: "",
			document: "",
			phone: "",
			street: "",
			house: "",
			complement: "",
			neighborhood: "",
			city: "",
			state: "",
			zipCode: "",
			email: "",
		},
		mode: "onChange",
	});

	useEffect(() => {
		if (patient) {
			form.reset(
				{
					fullName: patient?.person?.name || "",
					document: patient?.person?.document
						? unmaskCPF(patient?.person?.document)
						: "",
					phone: patient?.person?.phone
						? unmaskPhone(patient?.person?.phone)
						: "",
					street: patient?.person?.address?.street || "",
					house: patient?.person?.address?.house || "",
					complement: patient?.person?.address?.complement || "",
					neighborhood: patient?.person?.address?.neighborhood || "",
					city: patient?.person?.address?.city || "",
					state: patient?.person?.address?.state || "",
					zipCode: patient?.person?.address?.zipCode
						? unmaskPostalCode(patient?.person?.address?.zipCode)
						: "",
					email: patient.email || "",
				},
				{
					keepDirty: true,
					keepDirtyValues: true,
				},
			);

			form.trigger();
		}
	}, [patient, form.reset, form.trigger]);

	async function onSubmit(values) {
		const hasChanges = form.formState.isDirty;

		if (!hasChanges) {
			toast.warning("Nenhuma alteração detectada.");
			return;
		}

		const payload = {
			name: values.fullName,
			phone: unmaskPhone(values.phone),
			address: {
				street: values.street,
				house: values.house,
				complement: values.complement,
				neighborhood: values.neighborhood,
				city: values.city,
				state: values.state,
				zipCode: maskPostalCode(values.zipCode),
			},
		};

		try {
			const response = await api.put(
				`/persons/${patient?.person?.id}`,
				payload,
			);

			if (response.status !== 200) {
				throw new Error(
					response.data?.message ||
						"Erro ao atualizar dados pessoais do paciente.",
				);
			}

			toast.success("Dados pessoais atualizados com sucesso!");

			queryClient.invalidateQueries({
				queryKey: ["patients"],
			});

			queryClient.invalidateQueries({
				queryKey: ["patient", id],
			});
		} catch (error) {
			toast.error(error.message);
		}
	}

	function Header() {
		return (
			<header className="flex h-16 shrink-0 items-center gap-2">
				<div className="flex items-center gap-2 px-4">
					<SidebarTrigger className="-ml-1" />
					<Separator
						orientation="vertical"
						className="mx-2 data-[orientation=vertical]:h-4 "
					/>
					<Breadcrumb>
						<BreadcrumbList>
							<BreadcrumbItem className="hidden md:block">
								<BreadcrumbLink asChild>
									<Link to="/patients">Pacientes</Link>
								</BreadcrumbLink>
							</BreadcrumbItem>
							<BreadcrumbSeparator className="hidden md:block" />
							<BreadcrumbItem>
								<BreadcrumbLink asChild>
									<Link to="/patients/$id">{patient?.person?.name || "—"}</Link>
								</BreadcrumbLink>
							</BreadcrumbItem>
							<BreadcrumbSeparator className="hidden md:block" />
							<BreadcrumbItem>
								<BreadcrumbPage>Editar</BreadcrumbPage>
							</BreadcrumbItem>
						</BreadcrumbList>
					</Breadcrumb>
				</div>
			</header>
		);
	}

	const cityValue = form.watch("city");
	const stateValue = form.watch("state");

	if (isLoading)
		return (
			<>
				<Header />
				<Loading />
			</>
		);

	if (!patient) {
		return (
			<>
				<Header />
				<div className="flex flex-col items-center justify-center p-8">
					<h2 className="text-lg font-semibold">Paciente não encontrado</h2>
					<p className="text-muted-foreground">
						Tente novamente mais tarde ou contate o suporte.
					</p>
				</div>
			</>
		);
	}

	return (
		<>
			<Header />

			<div className="flex flex-col gap-4 p-4 pt-0">
				<div className="flex flex-col gap-1.5 border-b lg:flex-row">
					<Link
						className="flex items-center gap-1.5 px-4 py-2 text-sm transition-colors hover:rounded-tl hover:rounded-tr hover:bg-sidebar"
						params={{
							id,
						}}
						to="/patients/$id"
					>
						Visão geral
					</Link>
					<Link
						className="border-b-2 border-b-primary px-4 py-2 text-sm transition-colors hover:rounded-tl hover:rounded-tr hover:bg-sidebar"
						params={{
							id,
						}}
						to="."
					>
						Editar informações
					</Link>
					<Link
						className="flex items-center gap-1.5 px-4 py-2 text-sm transition-colors hover:rounded-tl hover:rounded-tr hover:bg-sidebar"
						params={{
							id,
						}}
						to="/patients/$id/appointments"
					>
						Histórico de consultas
					</Link>
				</div>
			</div>

			<div className="flex flex-1 flex-col gap-4 p-4 pt-0">
				<Form {...form}>
					<form
						onSubmit={form.handleSubmit(onSubmit)}
						className="space-y-6 mx-auto w-full"
					>
						<Card className="p-6 w-full">
							<div className="grid grid-cols-1 lg:grid-cols-[1fr_auto_1fr] gap-6">
								<div className="space-y-4">
									<h2 className="text-lg font-bold mb-4">Dados Pessoais</h2>

									<FormField
										control={form.control}
										name="email"
										render={({ field }) => (
											<FormItem>
												<FormLabel>Email</FormLabel>
												<FormControl>
													<Input
														disabled
														placeholder="email@exemplo.com"
														{...field}
													/>
												</FormControl>
												<FormMessage />
											</FormItem>
										)}
									/>

									<FormField
										control={form.control}
										name="document"
										render={({ field }) => (
											<FormItem>
												<FormLabel>Documento (CPF)</FormLabel>
												<FormControl>
													<Input
														placeholder="000.000.000-00"
														disabled
														onChange={(e) =>
															field.onChange(unmaskCPF(e.target.value))
														}
														value={maskCPF(field.value)}
													/>
												</FormControl>
												<FormMessage />
											</FormItem>
										)}
									/>

									<FormField
										control={form.control}
										name="fullName"
										render={({ field }) => (
											<FormItem>
												<FormLabel>Nome completo</FormLabel>
												<FormControl>
													<Input placeholder="Asteróide Silverio" {...field} />
												</FormControl>
												<FormMessage />
											</FormItem>
										)}
									/>

									<FormField
										control={form.control}
										name="phone"
										render={({ field }) => (
											<FormItem>
												<FormLabel>Telefone</FormLabel>
												<FormControl>
													<Input
														placeholder="(71) 99999-9999"
														onChange={(e) =>
															field.onChange(unmaskPhone(e.target.value))
														}
														value={maskPhone(field.value)}
													/>
												</FormControl>
												<FormMessage />
											</FormItem>
										)}
									/>
								</div>

								<Separator
									orientation="vertical"
									className="hidden lg:block h-full"
								/>

								<div className="space-y-4">
									<h2 className="text-lg font-bold mb-4">Endereço</h2>

									<div className="grid grid-cols-4 gap-4">
										<FormField
											control={form.control}
											name="zipCode"
											render={({ field }) => (
												<FormItem>
													<FormLabel>CEP</FormLabel>
													<FormControl>
														<Input
															placeholder="40000-000"
															onChange={(e) =>
																field.onChange(unmaskPostalCode(e.target.value))
															}
															value={maskPostalCode(field.value)}
														/>
													</FormControl>
													<FormMessage />
												</FormItem>
											)}
										/>

										<FormField
											control={form.control}
											name="street"
											render={({ field }) => (
												<FormItem className="col-span-3">
													<FormLabel>Logradouro</FormLabel>
													<FormControl>
														<Input
															placeholder="Av. Tancredo Neves"
															{...field}
														/>
													</FormControl>
													<FormMessage />
												</FormItem>
											)}
										/>
									</div>

									<div className="grid grid-cols-6 gap-4">
										<FormField
											control={form.control}
											name="house"
											render={({ field }) => (
												<FormItem>
													<FormLabel>Número</FormLabel>
													<FormControl>
														<Input placeholder="620" {...field} />
													</FormControl>
													<FormMessage />
												</FormItem>
											)}
										/>

										<FormField
											control={form.control}
											name="complement"
											render={({ field }) => (
												<FormItem className="col-span-5">
													<FormLabel>Complemento</FormLabel>
													<FormControl>
														<Input placeholder="Apto 101" {...field} />
													</FormControl>
													<FormMessage />
												</FormItem>
											)}
										/>
									</div>

									<FormField
										control={form.control}
										name="neighborhood"
										render={({ field }) => (
											<FormItem>
												<FormLabel>Bairro</FormLabel>
												<FormControl>
													<Input placeholder="Caminho das Árvores" {...field} />
												</FormControl>
												<FormMessage />
											</FormItem>
										)}
									/>

									<div className="grid grid-cols-2 gap-4 items-start">
										<FormField
											control={form.control}
											name="city"
											render={({ field }) => (
												<FormItem>
													<FormLabel>Cidade</FormLabel>
													<FormControl>
														<CityPicker
															value={cityValue || ""}
															state={stateValue}
															onChange={(value) =>
																value &&
																value !== "" &&
																value !== cityValue &&
																form.setValue("city", value, {
																	shouldValidate: true,
																	shouldDirty: true,
																})
															}
														/>
													</FormControl>
													<FormMessage />
												</FormItem>
											)}
										/>

										<FormField
											control={form.control}
											name="state"
											render={({ field }) => (
												<FormItem>
													<FormLabel>Estado</FormLabel>
													<FormControl>
														<StatePicker
															fullNames
															value={stateValue || ""}
															onChange={(value) => {
																if (
																	value &&
																	value !== stateValue &&
																	value !== ""
																) {
																	form.setValue("state", value, {
																		shouldValidate: true,
																		shouldDirty: true,
																	});
																	form.setValue("city", "", {
																		shouldValidate: true,
																		shouldDirty: true,
																	});
																}
															}}
														/>
													</FormControl>
													<FormMessage />
												</FormItem>
											)}
										/>
									</div>
								</div>
							</div>

							<Separator className="my-2" />

							<div className="flex justify-end mt-0">
								<Button
									type="submit"
									disabled={!form.formState.isValid || !form.formState.isDirty}
								>
									Salvar alterações
								</Button>
							</div>
						</Card>
					</form>
				</Form>
			</div>
		</>
	);
}
