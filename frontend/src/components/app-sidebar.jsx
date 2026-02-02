import logoImg from "@/assets/logo.png";
import {
	Sidebar,
	SidebarContent,
	SidebarFooter,
	SidebarHeader,
	SidebarMenu,
	SidebarMenuButton,
	SidebarMenuItem,
	SidebarRail,
} from "@/components/ui/sidebar";
import { useAuth } from "@/hooks/use-auth";
import { Link } from "@tanstack/react-router";
import {
	Ban,
	BriefcaseMedical,
	ClipboardList,
	HeartHandshake,
	ListCheck,
	Medal,
	PersonStanding,
	Settings,
} from "lucide-react";
import { useMemo } from "react";
import { NavMain } from "./nav/nav-main";
import { NavUser } from "./nav/nav-user";

export function AppSidebar({ ...props }) {
	const { user, hasRole } = useAuth();

	const adminNav = useMemo(() => {
		return [
			{
				title: "Todas as Consultas",
				url: "/appointments",
				icon: ClipboardList,
				role: "ADMIN",
			},
			{
				title: "Médicos",
				url: "/doctors",
				icon: BriefcaseMedical,
				role: "ADMIN",
			},
			{
				title: "Pacientes",
				url: "/patients",
				icon: HeartHandshake,
				role: "ADMIN",
			},
		];
	}, []);

	const doctorNav = useMemo(() => {
		return [
			{
				title: "Minhas Informações",
				url: "/doctors/me",
				icon: PersonStanding,
				role: "DOCTOR",
			},
			{
				title: "Minhas Consultas",
				url: "/appointments/me",
				icon: PersonStanding,
				role: "DOCTOR",
			},
		];
	}, []);

	const patientNav = useMemo(() => {
		return [
			{
				title: "Minhas Informações",
				url: "/patients/me",
				icon: PersonStanding,
				role: "PATIENT",
			},
			{
				title: "Agendar Consultas",
				url: "/appointments/schedule",
				icon: Medal,
				role: "PATIENT",
			},
		];
	}, []);

	const mainNav = useMemo(() => {
		return [
			{
				title: "Sair",
				url: "/auth/logout",
				className: "text-red-400",
				icon: Ban,
			},
		];
	}, []);

	return (
		<Sidebar collapsible="icon" variant="inset" {...props}>
			<SidebarHeader>
				<SidebarMenu>
					<SidebarMenuItem>
						<SidebarMenuButton asChild size="lg">
							<Link to="/">
								<img
									alt=""
									className="flex aspect-square size-8 items-center justify-center rounded-lg"
									src={logoImg}
								/>
								<div className="grid flex-1 text-left text-sm leading-tight">
									<span className="truncate font-semibold">IFB-A</span>
									<span className="truncate text-xs">Gestão</span>
								</div>
							</Link>
						</SidebarMenuButton>
					</SidebarMenuItem>
				</SidebarMenu>
			</SidebarHeader>

			<SidebarContent>
				<div className="flex flex-1 flex-col gap-6 justify-between">
					<div>
						{hasRole("ADMIN") && (
							<NavMain name="Administrador" items={adminNav} />
						)}
						{hasRole("DOCTOR") && <NavMain name="Médico" items={doctorNav} />}
						{hasRole("PATIENT") && (
							<NavMain name="Paciente" items={patientNav} />
						)}
					</div>

					<NavMain name="Minha Conta" items={mainNav} />
				</div>
			</SidebarContent>

			<SidebarFooter>
				<NavUser />
			</SidebarFooter>

			<SidebarRail />
		</Sidebar>
	);
}
