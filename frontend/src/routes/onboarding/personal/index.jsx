import CityPicker from "@/components/brazil-city-picker";
import StatePicker from "@/components/brazil-state-picker";
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
import { useOnboarding } from "@/hooks/use-onboarding";
import { maskPostalCode, unmaskPostalCode } from "@/utils/cep-utils";
import { maskCPF, unmaskCPF, validateCPF } from "@/utils/cpf-utils";
import { maskPhone, unmaskPhone } from "@/utils/phone-utils";
import { hasSelectedProfile } from "@/utils/validators";
import { zodResolver } from "@hookform/resolvers/zod";
import { createFileRoute, redirect } from "@tanstack/react-router";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import z from "zod";
import { NavigationControls } from "../-components/navigation-controls";

export const Route = createFileRoute("/onboarding/personal/")({
	component: PersonalOnboardingPage,
	beforeLoad: ({ context }) => {
		const { onboarding } = context;

		const onboardingData = onboarding.onboardingData;

		if (!hasSelectedProfile(onboardingData)) {
			throw redirect({ to: "/onboarding" });
		}
	},
});

const personalInformationFormSchema = z.object({
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

export default function PersonalOnboardingPage() {
	const { onboardingData, updateStepData, setCurrentStep } = useOnboarding();

	const form = useForm({
		mode: "onChange",
		resolver: zodResolver(personalInformationFormSchema),
		defaultValues: {
			// fullName: "",
			// document: "",
			// phone: "",
			// street: "",
			// house: "",
			// complement: "",
			// neighborhood: "",
			// city: "",
			// state: "",
			// zipCode: "",
			fullName: "Severino",
			document: "12345678909",
			phone: "71986148069",
			street: "Rua Asteróide",
			house: "123",
			complement: "Apto 456",
			neighborhood: "Bairro Asteróide",
			city: "Salvador",
			state: "BA",
			zipCode: "12345678",
		},
	});

	useEffect(() => {
		const { personal, address } = onboardingData.personal;

		if (personal || address) {
			form.reset(
				{
					fullName: personal?.name || "",
					document: personal?.document || "",
					phone: personal?.phone || "",
					street: address?.street || "",
					house: address?.house || "",
					complement: address?.complement || "",
					neighborhood: address?.neighborhood || "",
					city: address?.city || "",
					state: address?.state || "",
					zipCode: unmaskPostalCode(address?.zipCode) || "",
				},
				{ shouldValidate: true, keepDirty: false, keepTouched: false },
			);

			form.trigger();
		}
	}, [form.reset, form.trigger, onboardingData]);

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
			updateStepData("personal", {
				personal: {
					name: values.fullName,
					document: unmaskCPF(values.document),
					phone: unmaskPhone(values.phone),
				},
				address: {
					street: values.street,
					house: values.house,
					complement: values.complement,
					neighborhood: values.neighborhood,
					city: values.city,
					state: values.state,
					zipCode: maskPostalCode(values.zipCode),
				},
			});

			return true;
		}

		return false;
	}

	const cityValue = form.watch("city");
	const stateValue = form.watch("state");

	return (
		<Form {...form}>
			<form onSubmit={form.handleSubmit(handleSaveData)}>
				<Card className="flex flex-col md:flex-row w-full max-w-5xl mx-auto overflow-hidden p-0">
					<div className="w-full md:w-1/2 p-6 border-r">
						<h2 className="text-lg font-bold mb-4">Dados pessoais</h2>
						<div className="space-y-4">
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
								name="document"
								render={({ field }) => (
									<FormItem>
										<FormLabel>Documento (CPF)</FormLabel>
										<FormControl>
											<Input
												placeholder="000.000.000-00"
												onChange={(e) => {
													field.onChange(unmaskCPF(e.target.value));
												}}
												value={maskCPF(field.value)}
											/>
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
												onChange={(e) => {
													field.onChange(unmaskPhone(e.target.value));
												}}
												value={maskPhone(field.value)}
											/>
										</FormControl>
										<FormMessage />
									</FormItem>
								)}
							/>
						</div>
					</div>

					<div className="w-full md:w-1/2 p-6">
						<h2 className="text-lg font-bold mb-4">Endereço</h2>
						<div className="grid grid-cols-1 gap-4">
							<FormField
								control={form.control}
								name="street"
								render={({ field }) => (
									<FormItem>
										<FormLabel>Logradouro</FormLabel>
										<FormControl>
											<Input placeholder="Av. Tancredo Neves" {...field} />
										</FormControl>
										<FormMessage />
									</FormItem>
								)}
							/>
							<div className="grid grid-cols-2 gap-4">
								<FormField
									control={form.control}
									name="house"
									render={({ field }) => (
										<FormItem>
											<FormLabel>Número</FormLabel>
											<FormControl>
												<Input placeholder="620" {...field} />
											</FormControl>
										</FormItem>
									)}
								/>
								<FormField
									control={form.control}
									name="complement"
									render={({ field }) => (
										<FormItem>
											<FormLabel>Complemento</FormLabel>
											<FormControl>
												<Input placeholder="Mundo Plaza" {...field} />
											</FormControl>
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
							<div className="grid grid-cols-[1fr_80px] gap-4 items-start">
								<FormField
									control={form.control}
									name="city"
									render={({ field }) => (
										<FormItem>
											<FormLabel>Cidade</FormLabel>
											<FormControl>
												<CityPicker
													value={cityValue || ""}
													state={form.watch("state")}
													onChange={(value) =>
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
													value={stateValue || ""}
													onChange={(value) => {
														value !== "" &&
															value !== stateValue &&
															form.setValue("state", value, {
																shouldValidate: true,
																shouldDirty: true,
															});

														value !== "" &&
															value !== stateValue &&
															form.setValue("city", "", {
																shouldValidate: true,
																shouldDirty: true,
															});
													}}
												/>
											</FormControl>
										</FormItem>
									)}
								/>
							</div>
							<FormField
								control={form.control}
								name="zipCode"
								render={({ field }) => (
									<FormItem>
										<FormLabel>CEP</FormLabel>
										<FormControl>
											<Input
												placeholder="40000-000"
												onChange={(e) => {
													field.onChange(unmaskPostalCode(e.target.value));
												}}
												value={maskPostalCode(field.value)}
											/>
										</FormControl>
										<FormMessage />
									</FormItem>
								)}
							/>
						</div>
					</div>
				</Card>

				<NavigationControls
					canProceed={form.formState.isValid}
					prePreviousHook={async () => await handleSaveData(true)}
					preNextHook={onNext}
				/>
			</form>
		</Form>
	);
}
