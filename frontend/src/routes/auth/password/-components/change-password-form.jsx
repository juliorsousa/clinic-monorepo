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
import { api } from "@/lib/api";
import { zodResolver } from "@hookform/resolvers/zod";
import { AxiosError } from "axios";
import { KeyRound, Loader2, Sparkles } from "lucide-react";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import { z } from "zod";

/* ---------------------------------------------
   Password validation rules (nova senha)
---------------------------------------------- */
const passwordSchema = z
	.string({ required_error: "A senha é obrigatória." })
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

/* ---------------------------------------------
   Form schema
---------------------------------------------- */
const changePasswordFormSchema = z
	.object({
		oldPassword: z.string({
			required_error: "A senha atual é obrigatória.",
		}),

		newPassword: passwordSchema,

		confirmNewPassword: z.string({
			required_error: "A confirmação da nova senha é obrigatória.",
		}),
	})
	.refine((data) => data.newPassword === data.confirmNewPassword, {
		message: "As senhas não coincidem.",
		path: ["confirmNewPassword"],
	})
	.refine((data) => data.oldPassword !== data.newPassword, {
		message: "A nova senha deve ser diferente da senha atual.",
		path: ["newPassword"],
	});

export function ChangePasswordForm() {
	const form = useForm({
		resolver: zodResolver(changePasswordFormSchema),
		reValidateMode: "onSubmit",
		defaultValues: {
			oldPassword: "",
			newPassword: "",
			confirmNewPassword: "",
		},
	});

	async function onHandleSubmit(data) {
		try {
			await api.post("/auth/change-password", data);
			toast.success("Senha alterada com sucesso!");
			form.reset();

			location.replace("/");
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
				<FormField
					control={form.control}
					name="oldPassword"
					render={({ field }) => (
						<FormItem>
							<FormLabel>Senha atual</FormLabel>
							<FormControl>
								<Input
									type="password"
									placeholder="Senha atual"
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
					name="newPassword"
					render={({ field }) => (
						<FormItem>
							<FormLabel>Nova senha</FormLabel>
							<FormControl>
								<Input
									type="password"
									placeholder="Nova senha forte"
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
					name="confirmNewPassword"
					render={({ field }) => (
						<FormItem>
							<FormLabel>Confirmar nova senha</FormLabel>
							<FormControl>
								<Input
									type="password"
									placeholder="Repita a nova senha"
									className="border mt-1"
									{...field}
								/>
							</FormControl>
							<FormMessage />
						</FormItem>
					)}
				/>

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
						<KeyRound className="size-4" />
					)}
					Alterar senha
				</Button>
			</form>
		</Form>
	);
}
