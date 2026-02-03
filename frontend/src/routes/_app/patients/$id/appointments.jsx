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
import { PatientAppointmentsTable } from "./-components/patient-appointments-table";

export const Route = createFileRoute("/_app/patients/$id/appointments")({
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
								<BreadcrumbLink asChild>
									<Link to="/patients/$id">{patient?.person?.name || "—"}</Link>
								</BreadcrumbLink>
							</BreadcrumbItem>
							<BreadcrumbSeparator className="hidden md:block" />
							<BreadcrumbItem>
								<BreadcrumbPage>Histórico de consultas</BreadcrumbPage>
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
						className="flex items-center gap-1.5 px-4 py-2 text-sm transition-colors hover:rounded-tl hover:rounded-tr hover:bg-sidebar"
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
						className="border-b-2 border-b-primary px-4 py-2 text-sm transition-colors hover:rounded-tl hover:rounded-tr hover:bg-sidebar"
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
							<div className="flex flex-col gap-4">
								<PatientAppointmentsTable patientId={patient.id} />
							</div>
						)}
					</>
				)}
			</div>
		</>
	);
}
