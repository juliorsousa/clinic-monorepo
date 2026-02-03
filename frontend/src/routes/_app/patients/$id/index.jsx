import { Loading } from "@/components/loading";
import {
	Breadcrumb,
	BreadcrumbItem,
	BreadcrumbLink,
	BreadcrumbList,
	BreadcrumbPage,
	BreadcrumbSeparator,
} from "@/components/ui/breadcrumb";
import { Separator } from "@/components/ui/separator";
import { SidebarTrigger } from "@/components/ui/sidebar";
import { useAuth } from "@/hooks/use-auth";
import { usePatient } from "@/lib/hooks/use-patient";
import { maskCPF } from "@/utils/cpf-utils";
import { maskPhone } from "@/utils/phone-utils";
import { Link, createFileRoute } from "@tanstack/react-router";
import {
	AtSign,
	Calendar,
	Hash,
	IdCard,
	Layers,
	Mail,
	MapPin,
	Phone,
} from "lucide-react";

export const Route = createFileRoute("/_app/patients/$id/")({
	component: Patient,
});

function Patient() {
	const { getSpecificRoleId } = useAuth();
	const { id: pathId } = Route.useParams();

	const specificRoleId = getSpecificRoleId("PATIENT");

	const id = pathId === "me" && specificRoleId ? specificRoleId : pathId;

	const { isLoading, data: patient } = usePatient(id);

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
								<BreadcrumbPage>{patient?.person?.name || "—"}</BreadcrumbPage>
							</BreadcrumbItem>
						</BreadcrumbList>
					</Breadcrumb>
				</div>
			</header>
		);
	}

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
						className="border-b-2 border-b-primary px-4 py-2 text-sm transition-colors hover:rounded-tl hover:rounded-tr hover:bg-sidebar"
						to="."
					>
						Visão geral
					</Link>
					<Link
						className="flex items-center gap-1.5 px-4 py-2 text-sm transition-colors hover:rounded-tl hover:rounded-tr hover:bg-sidebar"
						params={{
							id,
						}}
						to="/patients/$id/edit"
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
				{isLoading ? (
					<Loading />
				) : (
					<>
						{patient && (
							<div className="flex w-full flex-1 flex-col gap-3">
								<section className="flex flex-col gap-1">
									<h2 className="text-lg font-semibold tracking-tight">
										Informações Pessoais
									</h2>
									<div className="flex flex-col gap-0 border rounded-lg bg-card overflow-hidden">
										<div className="grid md:grid-cols-2 lg:grid-cols-3 border-b-0">
											<div className="flex items-center gap-3 border-b p-6 text-muted-foreground md:border-r lg:border-b-0">
												<div className="rounded-full bg-sidebar p-2 text-primary">
													<AtSign className="h-5 w-5" />
												</div>
												<div className="flex flex-col">
													<span className="text-xs font-medium uppercase text-muted-foreground/70">
														ID do Sistema
													</span>
													<span className="text-xs font-mono text-foreground/80">
														{patient?.id?.split("-")?.[0] || "—"}...
													</span>
												</div>
											</div>

											<div className="flex items-center gap-3 border-b p-6 text-muted-foreground lg:border-r lg:border-b-0">
												<div className="rounded-full bg-sidebar p-2 text-primary">
													<IdCard className="h-5 w-5" />
												</div>
												<div className="flex flex-col">
													<span className="text-xs font-medium uppercase text-muted-foreground/70">
														Nome Completo
													</span>
													<span className="text-sm font-medium text-foreground">
														{patient?.person?.name || "—"}
													</span>
												</div>
											</div>

											<div className="flex items-center gap-3 p-6 text-muted-foreground">
												<div className="rounded-full bg-sidebar p-2 text-primary">
													<Hash className="h-5 w-5" />
												</div>
												<div className="flex flex-col">
													<span className="text-xs font-medium uppercase text-muted-foreground/70">
														CPF / Documento
													</span>
													<span className="text-sm font-medium text-foreground">
														{patient?.person?.document
															? maskCPF(patient.person.document)
															: "—"}
													</span>
												</div>
											</div>
										</div>

										<div className="grid lg:grid-cols-2 border-t">
											<div className="flex items-center gap-3 p-6 text-muted-foreground lg:border-r">
												<div className="rounded-full bg-sidebar p-2 text-primary">
													<Phone className="h-5 w-5" />
												</div>
												<div className="flex flex-col">
													<span className="text-xs font-medium uppercase text-muted-foreground/70">
														Telefone
													</span>
													<span className="text-sm font-medium text-foreground">
														{patient?.person?.phone
															? maskPhone(patient.person.phone)
															: "—"}
													</span>
												</div>
											</div>

											<div className="flex items-center gap-3 p-6 text-muted-foreground">
												<div className="rounded-full bg-sidebar p-2 text-primary">
													<Mail className="h-5 w-5" />
												</div>
												<div className="flex flex-col">
													<span className="text-xs font-medium uppercase text-muted-foreground/70">
														Email
													</span>
													<span className="text-sm font-medium text-foreground">
														{patient?.person?.email || "—"}
													</span>
												</div>
											</div>
										</div>
									</div>
								</section>

								<section className="flex flex-col gap-1">
									<h2 className="text-lg font-semibold tracking-tight">
										Endereço Residencial
									</h2>
									<div className="grid rounded-lg border bg-card lg:grid-cols-2">
										<div className="flex items-center gap-3 border-b p-6 text-muted-foreground lg:border-r lg:border-b-0">
											<div className="rounded-full bg-sidebar p-2 text-primary">
												<Layers className="h-5 w-5" />
											</div>
											<div className="flex flex-col">
												<span className="text-xs font-medium uppercase text-muted-foreground/70">
													Logradouro
												</span>
												<span className="text-sm font-medium text-foreground">
													{patient?.person?.address?.street || "—"},{" "}
													{patient?.person?.address?.house || "—"}
													{patient?.person?.address?.complement &&
														` — ${patient?.person?.address?.complement}`}
												</span>
												<span className="text-xs text-muted-foreground">
													{patient?.person?.address?.neighborhood || "—"}
												</span>
											</div>
										</div>

										<div className="flex items-center gap-3 p-6 text-muted-foreground">
											<div className="rounded-full bg-sidebar p-2 text-primary">
												<MapPin className="h-5 w-5" />{" "}
											</div>
											<div className="flex flex-col">
												<span className="text-xs font-medium uppercase text-muted-foreground/70">
													Localidade
												</span>
												<span className="text-sm font-medium text-foreground">
													{patient?.person?.address?.city || "—"} —{" "}
													{patient?.person?.address?.state || "—"}
												</span>
												<span className="text-xs text-muted-foreground">
													CEP: {patient?.person?.address?.zipCode || "—"}
												</span>
											</div>
										</div>
									</div>
								</section>
							</div>
						)}
					</>
				)}
			</div>
		</>
	);
}
