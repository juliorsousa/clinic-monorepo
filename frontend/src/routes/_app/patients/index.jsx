import { Loading } from "@/components/loading";
import {
	Breadcrumb,
	BreadcrumbItem,
	BreadcrumbList,
	BreadcrumbPage,
} from "@/components/ui/breadcrumb";
import { Separator } from "@/components/ui/separator";
import { SidebarTrigger } from "@/components/ui/sidebar";
import { createFileRoute, redirect } from "@tanstack/react-router";
import { PatientsDataTable } from "./-components/patients-data-table";

export const Route = createFileRoute("/_app/patients/")({
	component: PatientsComponent,
	head: () => ({
		meta: [
			{
				title: "Pacientes | Clínica",
			},
		],
	}),
	beforeLoad: async ({ context }) => {
		const { auth } = context;

		if (auth.isAuthLoading) {
			return <Loading />;
		}

		if (!auth.hasRole("ADMIN")) {
			throw redirect({ to: "/" });
		}
	},
});

function PatientsComponent() {
	return (
		<>
			<header className="flex h-16 shrink-0 items-center gap-2">
				<div className="flex items-center gap-2 px-4">
					<SidebarTrigger className="-ml-1" />
					<Separator
						orientation="vertical"
						className="mx-2 data-[orientation=vertical]:h-4 "
					/>
					<Breadcrumb>
						<BreadcrumbList>
							<BreadcrumbItem>
								<BreadcrumbPage>Pacientes</BreadcrumbPage>
							</BreadcrumbItem>
						</BreadcrumbList>
					</Breadcrumb>
				</div>
			</header>
			<div className="flex flex-1 flex-col gap-4 p-4 pt-0">
				<PatientsDataTable />
			</div>
		</>
	);
}
