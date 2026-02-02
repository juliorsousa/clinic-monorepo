// import { Loading } from "@/components/loading";
// import {
// 	Breadcrumb,
// 	BreadcrumbItem,
// 	BreadcrumbList,
// 	BreadcrumbPage,
// } from "@/components/ui/breadcrumb";
// import { Separator } from "@/components/ui/separator";
// import { SidebarTrigger } from "@/components/ui/sidebar";
import { Outlet, createFileRoute, redirect } from "@tanstack/react-router";
import { LoadingPage } from "../loading";

export const Route = createFileRoute("/_app/")({
	component: Outlet,
	head: () => ({
		meta: [
			{
				title: "Página Inicial | Clínica",
			},
		],
	}),
	beforeLoad: ({ context }) => {
		const { auth } = context;

		if (auth.isAuthLoading) {
			return <LoadingPage />;
		}

		if (auth.hasRole("ADMIN")) {
			throw redirect({ to: "/patients" });
		}

		if (auth.hasRole("DOCTOR")) {
			throw redirect({ to: "/doctors/me" });
		}

		if (auth.hasRole("PATIENT")) {
			throw redirect({ to: "/patients/me" });
		}
	},
});

// function PunishmentsComponent() {
// 	return (
// 		<>
// 			<header className="flex h-16 shrink-0 items-center gap-2">
// 				<div className="flex items-center gap-2 px-4">
// 					<SidebarTrigger className="-ml-1" />
// 					<Separator
// 						orientation="vertical"
// 						className="mx-2 data-[orientation=vertical]:h-4 "
// 					/>
// 					<Breadcrumb>
// 						<BreadcrumbList>
// 							<BreadcrumbItem>
// 								<BreadcrumbPage>Página Inicial</BreadcrumbPage>
// 							</BreadcrumbItem>
// 						</BreadcrumbList>
// 					</Breadcrumb>
// 				</div>
// 			</header>
// 			<div className="flex flex-1 flex-col gap-4 p-4 pt-0">
// 				<Loading />
// 			</div>
// 		</>
// 	);
// }
