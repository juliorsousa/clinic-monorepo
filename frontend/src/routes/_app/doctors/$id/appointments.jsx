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
import { useDoctor } from "@/lib/hooks/use-doctor";
import { Link, createFileRoute } from "@tanstack/react-router";
import { DoctorAppointmentsTable } from "./-components/doctor-appointments-table";

export const Route = createFileRoute("/_app/doctors/$id/appointments")({
	component: Doctor,
});

function Doctor() {
	const { getSpecificRoleId } = useAuth();
	const { id: pathId } = Route.useParams();

	const specificRoleId = getSpecificRoleId("DOCTOR");

	const id = pathId === "me" && specificRoleId ? specificRoleId : pathId;

	const { isLoading, data: doctor } = useDoctor(id);

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
									<Link to="/doctors">Médicos</Link>
								</BreadcrumbLink>
							</BreadcrumbItem>
							<BreadcrumbSeparator className="hidden md:block" />
							<BreadcrumbItem>
								<BreadcrumbLink asChild>
									<Link to="/doctors/$id">{doctor?.person?.name || "—"}</Link>
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

	if (!doctor) {
		return (
			<>
				<Header />
				<div className="flex flex-col items-center justify-center p-8">
					<h2 className="text-lg font-semibold">Médico não encontrado</h2>
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
						to="/doctors/$id"
					>
						Visão geral
					</Link>
					<Link
						className="flex items-center gap-1.5 px-4 py-2 text-sm transition-colors hover:rounded-tl hover:rounded-tr hover:bg-sidebar"
						params={{
							id,
						}}
						to="/doctors/$id/edit"
					>
						Editar informações
					</Link>
					<Link
						className="border-b-2 border-b-primary px-4 py-2 text-sm transition-colors hover:rounded-tl hover:rounded-tr hover:bg-sidebar"
						params={{
							id,
						}}
						to="."
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
						{doctor && (
							<div className="flex flex-col gap-4">
								<DoctorAppointmentsTable doctorId={doctor.id} />
							</div>
						)}
					</>
				)}
			</div>
		</>
	);
}
