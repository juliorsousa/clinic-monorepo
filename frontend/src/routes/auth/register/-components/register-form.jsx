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
import { useAuth } from "@/hooks/use-auth";
import { zodResolver } from "@hookform/resolvers/zod";
import { Link } from "@tanstack/react-router";
import { AxiosError } from "axios";
import { Loader2, Sparkles, UserPlus } from "lucide-react";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import { z } from "zod";

const passwordSchema = z
	.string({
		required_error: "A senha é obrigatória.",
	})
	.min(6, { message: "A senha deve ter no mínimo 6 caracteres." })
	.max(60, { message: "A senha deve ter no máximo 60 caracteres." })
	.regex(/[a-z]/, {
		message: "A senha deve conter pelo menos uma letra minúscula.",
	})
	.regex(/[A-Z]/, {
		message: "A senha deve conter pelo menos uma letra maiúscula.",
	})
	.regex(/[0-9]/, {
		message: "A senha deve conter pelo menos um número.",
	})
	.regex(/[^a-zA-Z0-9]/, {
		message: "A senha deve conter pelo menos um caractere especial.",
	});

const registerFormSchema = z
	.object({
		email: z
			.string({ required_error: "O e-mail é obrigatório." })
			.email({ message: "O e-mail deve ser válido." })
			.min(3, { message: "O usuário precisa ter pelo menos 3 caracteres." }),

		password: passwordSchema,

		confirmPassword: z.string({
			required_error: "A confirmação da senha é obrigatória.",
		}),
	})
	.refine((data) => data.password === data.confirmPassword, {
		message: "As senhas não coincidem.",
		path: ["confirmPassword"],
	});

export function RegisterForm() {
	const form = useForm({
		resolver: zodResolver(registerFormSchema),
		reValidateMode: "onSubmit",
		defaultValues: {
			email: "",
			password: "",
			confirmPassword: "",
		},
	});

	const { register } = useAuth();

	async function onHandleSubmit(data) {
		try {
			await register(data);
			toast.success("Conta criada com sucesso!");
		} catch (ex) {
			if (ex instanceof AxiosError) {
				toast.error(
					ex?.response?.data?.message ??
						"Erro interno do servidor. Tente novamente.",
				);
			} else {
				toast.error("Erro interno do servidor. Tente novamente.");
			}
		}
	}

	return (
		<Form {...form}>
			<form
				onSubmit={form.handleSubmit(onHandleSubmit)}
				className="flex flex-col gap-4"
			>
				<div className="flex flex-col gap-3">
					<FormField
						control={form.control}
						name="email"
						render={({ field }) => (
							<FormItem>
								<FormLabel>E-mail</FormLabel>

								<FormControl>
									<Input
										type="text"
										placeholder="john@clinic.com"
										className="border mt-1"
										{...field}
									/>
								</FormControl>

								<FormMessage />
							</FormItem>
						)}
					/>

					<div className="after:border-border relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t" />

					<FormField
						control={form.control}
						name="password"
						render={({ field }) => (
							<FormItem>
								<FormLabel>Senha</FormLabel>

								<FormControl>
									<Input
										type="password"
										placeholder="Senha forte"
										className="border mt-1"
										{...field}
									/>
								</FormControl>

								<FormMessage />
							</FormItem>
						)}
					/>

					<FormField
						control={form.control}
						name="confirmPassword"
						render={({ field }) => (
							<FormItem>
								<FormLabel>Confirmar senha</FormLabel>

								<FormControl>
									<Input
										type="password"
										placeholder="Repita a senha"
										className="border mt-1"
										{...field}
									/>
								</FormControl>

								<FormMessage />
							</FormItem>
						)}
					/>
				</div>

				<div className="after:border-border relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t" />

				<Button
					disabled={form.formState.isSubmitting}
					type="submit"
					variant="outline"
					className="border bg-transparent border-amber-700 hover:bg-amber-500"
				>
					{form.formState.isSubmitting ? (
						<Loader2 className="size-4 animate-spin" />
					) : (
						<UserPlus className="size-4" />
					)}
					Criar conta
				</Button>

				<div className="after:border-border relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t">
					<span className="text-muted-foreground relative z-10 px-2 bg-card">
						Já possui uma conta?
					</span>
				</div>

				<Link to="/auth/login">
					<Card className="flex p-4 gap-4 group bg-sidebar hover:brightness-75 transition-all">
						<Sparkles className="size-6 text-amber-700" />
						<span className="text-sm font-medium text-amber-500">
							Entrar na minha conta
						</span>
					</Card>
				</Link>
			</form>
		</Form>
	);
}
