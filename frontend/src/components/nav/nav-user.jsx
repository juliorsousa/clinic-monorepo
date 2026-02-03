import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import {
	DropdownMenu,
	DropdownMenuContent,
	DropdownMenuGroup,
	DropdownMenuItem,
	DropdownMenuLabel,
	DropdownMenuSeparator,
	DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
	SidebarMenu,
	SidebarMenuButton,
	SidebarMenuItem,
	useSidebar,
} from "@/components/ui/sidebar";
import { useAuth } from "@/hooks/use-auth";
import { usePersonByUserId } from "@/lib/hooks/use-person";
import { ChevronsUpDown, LogOut } from "lucide-react";
import { useMemo } from "react";

export function NavUser() {
	const { isMobile } = useSidebar();
	const { isAuthLoading, user, signOut } = useAuth();

	if (isAuthLoading) return null;

	const { data: person } = usePersonByUserId(user?.id);

	const avatarInitials = useMemo(() => {
		if (!person) return "??";

		const names = person.name?.split(" ");

		if (names.length > 1) {
			const lastName = names.pop();

			return `${names[0][0]}${lastName[0]}`;
		}

		return `${names[0][0]}${names[0][1]}`;
	}, [person]);

	const roleNameMappings = {
		ADMIN: "Administrador",
		DOCTOR: "Médico",
		PATIENT: "Paciente",
	};

	return (
		<SidebarMenu>
			<SidebarMenuItem>
				<DropdownMenu>
					<DropdownMenuTrigger asChild>
						<SidebarMenuButton
							className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
							size="lg"
						>
							<Avatar className="h-8 w-8 rounded-lg">
								<AvatarFallback className="rounded-lg">
									{avatarInitials}
								</AvatarFallback>
							</Avatar>
							<div className="grid flex-1 text-left text-sm leading-tight">
								<span className="truncate font-semibold">{person?.name}</span>
								<span className="truncate text-xs">{user?.email}</span>
							</div>
							<ChevronsUpDown className="ml-auto size-4" />
						</SidebarMenuButton>
					</DropdownMenuTrigger>
					<DropdownMenuContent
						align="end"
						className="w-(--radix-dropdown-menu-trigger-width) min-w-56 rounded-lg"
						side={isMobile ? "bottom" : "right"}
						sideOffset={4}
					>
						<DropdownMenuLabel className="p-0 font-normal">
							<div className="flex items-center gap-2 px-1 py-1.5 text-left text-sm">
								<Avatar className="h-8 w-8 rounded-lg">
									<AvatarFallback className="rounded-lg">
										{avatarInitials}
									</AvatarFallback>
								</Avatar>
								<div className="grid flex-1 text-left text-sm leading-tight">
									<span className="truncate font-semibold">
										{person?.name || ""}
									</span>
									<span className="truncate text-xs">
										{user?.roles
											.map((role) => roleNameMappings[role?.role] || role?.role)
											.join(", ")}
									</span>
								</div>
							</div>
						</DropdownMenuLabel>
						<DropdownMenuSeparator />
						<DropdownMenuItem onClick={signOut}>
							<LogOut className="mr-2 size-4" />
							Sair
						</DropdownMenuItem>
					</DropdownMenuContent>
				</DropdownMenu>
			</SidebarMenuItem>
		</SidebarMenu>
	);
}
