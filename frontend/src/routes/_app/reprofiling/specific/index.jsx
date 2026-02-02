import SpecialtyPicker from "@/components/specialty-picker";
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
import { useReprofiling } from "@/hooks/use-reprofiling";
import { hasSelectedProfile } from "@/utils/validators";
import { zodResolver } from "@hookform/resolvers/zod";
import { createFileRoute, redirect } from "@tanstack/react-router";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import z from "zod";
import { ReprofilingNavigationControls } from "../-components/reprofiling-navigation-controls";

export const Route = createFileRoute("/_app/reprofiling/specific/")({
	component: MedicReprofilingPage,
	beforeLoad: ({ context }) => {
		const { reprofiling } = context;

		const reprofilingData = reprofiling.reprofilingData;

		if (!hasSelectedProfile(reprofilingData)) {
			throw redirect({ to: "/reprofiling" });
		}
	},
});

const medicRegistrySchema = z.object({
	credential: z
		.string()
		.regex(
			/^CRM?\s*[-\/]?\s*\d{4,7}(\s*[-\/]\s*[A-Z]{2})?$|^CRM\s*[-\/]?\s*[A-Z]{2}\s+\d{4,7}$/,
			"O formato da credencial é inválido.",
		)
		.min(1, "A credencial é obrigatória."),
	specialty: z.string().min(1, "Selecione uma especialidade."),
});

export default function MedicReprofilingPage() {
	const { updateReprofilingData, reprofilingData } = useReprofiling();

	const form = useForm({
		mode: "onChange",
		resolver: zodResolver(medicRegistrySchema),
		defaultValues: {
			credential: "",
			specialty: "",
		},
	});

	useEffect(() => {
		const specific = reprofilingData.specific;

		if (specific && (specific.credential || specific.specialty)) {
			form.reset(
				{
					credential: specific.credential || "",
					specialty: specific.specialty || "",
				},
				{ shouldValidate: true, keepDirty: false, keepTouched: false },
			);

			form.trigger();
		}
	}, [form.reset, form.trigger, reprofilingData]);

	async function onNext() {
		const ok = handleSaveData(false);
		if (!ok) return false;

		await new Promise((resolver) => setTimeout(resolver, 0));
		return true;
	}

	async function handleSaveData(leaving = false) {
		const values = form.getValues();
		const isValid = form.formState.isValid;

		if (isValid || leaving) {
			updateReprofilingData("specific", {
				credential: values.credential,
				specialty: values.specialty,
			});

			return true;
		}

		return false;
	}

	const specialtyValue = form.watch("specialty");

	return (
		<Form {...form}>
			<form onSubmit={form.handleSubmit(handleSaveData)}>
				<Card className="flex flex-col md:flex-row w-full max-w-5xl mx-auto overflow-hidden p-0 shadow-lg rounded-xl">
					<div className="w-full md:w-1/2 p-8 border-r">
						<h2 className="text-xl font-bold mb-4">
							Confirme seu novo perfil médico
						</h2>

						<p className="mb-4">
							Estamos quase lá! Nesta etapa, você nos ajuda a validar sua
							atuação profissional para garantir um ambiente seguro e confiável
							para pacientes e colegas.
						</p>

						<p className="mb-4">
							Sua <strong>credencial profissional</strong> confirma que você
							está habilitado(a) a exercer a medicina no Brasil.
						</p>

						<p className="mb-4">
							Já sua <strong>especialidade</strong> nos permite direcionar
							melhor oportunidades, pacientes e conexões alinhadas à sua área de
							atuação.
						</p>

						<p className="text-sm text-primary">
							Todas essas informações são armazenadas com segurança e usadas
							exclusivamente para validação profissional dentro da plataforma.
						</p>
					</div>

					<div className="w-full md:w-1/2 p-8">
						<h2 className="text-xl font-bold mb-4">
							Suas informações profissionais
						</h2>

						<div className="space-y-5">
							<FormField
								control={form.control}
								name="credential"
								render={({ field }) => (
									<FormItem>
										<FormLabel>Credencial profissional (CRM)</FormLabel>
										<FormControl>
											<Input
												placeholder="Ex: CRM-123456 ou CRM 123456/BA"
												{...field}
												className="h-11"
											/>
										</FormControl>
										<FormMessage />
									</FormItem>
								)}
							/>

							<FormField
								control={form.control}
								name="specialty"
								render={({ field }) => (
									<FormItem>
										<FormLabel>Especialidade médica</FormLabel>
										<FormControl>
											<SpecialtyPicker
												value={specialtyValue || ""}
												onChange={(value) =>
													value !== "" &&
													value !== specialtyValue &&
													form.setValue("specialty", value, {
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
						</div>
					</div>
				</Card>

				<ReprofilingNavigationControls
					canProceed={form.formState.isValid}
					prePreviousHook={async () => await handleSaveData(true)}
					preNextHook={onNext}
				/>
			</form>
		</Form>
	);
}
