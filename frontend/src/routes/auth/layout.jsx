import { Card } from "@/components/ui/card";
import { Outlet, createFileRoute, useMatches } from "@tanstack/react-router";
import { AnimatePresence, m, motion } from "framer-motion";

import coverImg from "@/assets/login-cover.png";
import logoImg from "@/assets/logo.png";

export const Route = createFileRoute("/auth")({
	component: AuthLayout,
});

function AuthLayout() {
	const matches = useMatches();

	const authMeta = matches
		.flatMap((meta) => meta.meta ?? [])
		.reduce((current, meta) => {
			if (meta.name === "auth:title") current.title = meta.content;
			if (meta.name === "auth:subtitle") current.subtitle = meta.content;
			if (meta.name === "auth:description") current.description = meta.content;
			return current;
		}, {});

	return (
		<div className="h-screen w-full flex items-center justify-center bg-muted/30 p-6">
			<Card className="flex flex-row w-full max-w-5xl overflow-hidden rounded-xl shadow-lg p-0">
				<div className="hidden md:flex w-1/2">
					<img
						src={coverImg}
						alt="Login cover"
						className="w-full h-full object-cover resize-none"
					/>
				</div>

				<div className="relative flex w-full md:w-1/2 items-center justify-center px-6 py-6">
					<AnimatePresence mode="wait">
						<motion.div
							key={location.pathname}
							initial={{ opacity: 0, x: 80 }}
							animate={{ opacity: 1, x: 0 }}
							exit={{ opacity: 0, x: -80 }}
							transition={{ duration: 0.45, ease: "easeInOut" }}
							className="w-full max-w-sm flex flex-col gap-6"
						>
							<header className="flex flex-col gap-4 items-center text-center">
								<img src={logoImg} alt="Clinic Logo" width={64} height={64} />

								<div className="space-y-1">
									<h2 className="text-2xl font-bold">
										{authMeta?.title ?? "Clínica IFB-A"}
									</h2>

									{authMeta?.description && (
										<p className="text-amber-600 text-sm">
											{authMeta.description}
										</p>
									)}

									{authMeta?.subtitle && (
										<div className="mt-4 after:border-border relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t">
											<span className="text-muted-foreground relative z-10 px-2 bg-card">
												{authMeta.subtitle}
											</span>
										</div>
									)}
								</div>
							</header>

							<Outlet />
						</motion.div>
					</AnimatePresence>
				</div>
			</Card>
		</div>
	);
}
