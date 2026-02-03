import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area";
import {
	appointmentStatusIconMap,
	appointmentStatusLabelMap,
} from "@/lib/constants";
import { useDoctorAppointments } from "@/lib/hooks/use-doctor-appointments";
import { useSummarizedPatient } from "@/lib/hooks/use-summarized-patient";
import { cn } from "@/lib/utils";
import { Fragment, useEffect, useMemo, useRef } from "react";

function AppointmentCard({ appointment }) {
	const { data: patient } = useSummarizedPatient(appointment.patientId);

	const time = new Date(appointment.scheduledTo).toLocaleTimeString([], {
		hour: "2-digit",
		minute: "2-digit",
	});

	const statusStyles = {
		SCHEDULED: "bg-blue-950/40 text-blue-400 border-blue-800/50",
		CONFIRMED: "bg-indigo-950/40 text-indigo-400 border-indigo-800/50",
		ONGOING:
			"bg-amber-950/40 text-amber-400 border-amber-700/50 ring-1 ring-amber-500/20",
		COMPLETED: "bg-emerald-950/40 text-emerald-400 border-emerald-800/50",
		CANCELLED: "bg-slate-900 text-slate-500 border-slate-800 opacity-50 italic",
	};

	return (
		<div
			className={cn(
				"flex flex-col gap-1 p-2.5 rounded-lg border shadow-lg transition-all hover:scale-[1.02] active:scale-[0.98] cursor-pointer",
				statusStyles[appointment.status] || "bg-slate-900 border-slate-800",
			)}
		>
			<div className="flex items-center justify-between">
				<span className="font-bold tabular-nums text-[11px] tracking-tight">
					{time}
				</span>
				<div className="opacity-70 scale-90">
					{appointmentStatusIconMap[appointment.status]}
				</div>
			</div>
			<div className="font-bold truncate text-[10px] uppercase tracking-widest text-white/90">
				{patient?.name ?? "..."}
			</div>
			<div className="text-[9px] text-slate-400 truncate">
				{appointmentStatusLabelMap[appointment.status]}
			</div>
		</div>
	);
}

export default function DoctorAppointmentsDiaryGrid({ doctorId }) {
	const { data } = useDoctorAppointments(doctorId, 0, 200);
	const appointments = data?.content ?? [];
	const scrollRef = useRef(null);

	const now = new Date();
	const todayStr = now.toDateString();
	const currentHour = now.getHours();

	useEffect(() => {
		const scrollContainer = scrollRef.current?.querySelector(
			"[data-radix-scroll-area-viewport]",
		);
		if (scrollContainer) {
			const targetElement = scrollContainer.querySelector(
				`[data-hour="${currentHour}"]`,
			);
			if (targetElement) {
				const centerOffset = scrollContainer.clientHeight / 2 - 70;
				scrollContainer.scrollTop = targetElement.offsetTop - centerOffset;
			}
		}
	}, [currentHour]);

	const days = useMemo(() => {
		const start = new Date();
		start.setDate(start.getDate() - start.getDay());
		start.setHours(0, 0, 0, 0);
		return Array.from({ length: 7 }).map((_, i) => {
			const date = new Date(start);
			date.setDate(start.getDate() + i);
			return date;
		});
	}, []);

	const hours = Array.from({ length: 13 }).map((_, i) => 7 + i);

	const cellAppointments = (day, hour) => {
		return appointments.filter((appointment) => {
			const date = new Date(appointment.scheduledTo);
			return (
				date.getFullYear() === day.getFullYear() &&
				date.getMonth() === day.getMonth() &&
				date.getDate() === day.getDate() &&
				date.getHours() === hour
			);
		});
	};

	return (
		<div className="w-full h-[750px] border border-slate-800 rounded-2xl flex flex-col bg-slate-950 text-slate-200 overflow-hidden shadow-2xl">
			<div className="grid grid-cols-[80px_repeat(7,1fr)] border-b border-slate-800 bg-slate-900/80 shrink-0 min-w-[900px]">
				<div className="p-3 border-r border-slate-800 bg-slate-950/50" />
				{days.map((day) => {
					const isToday = day.toDateString() === todayStr;
					const isPast = day < now && !isToday;

					return (
						<div
							key={day.toISOString()}
							className={cn(
								"p-3 text-center border-r border-slate-800 last:border-r-0 relative transition-colors",
								isToday && "bg-indigo-500/5",
								isPast &&
									"bg-black/40 grayscale-[0.5] pointer-events-none opacity-40",
							)}
						>
							{isToday && (
								<div className="absolute top-0 left-0 right-0 h-1 bg-indigo-500 shadow-[0_0_10px_rgba(99,102,241,0.5)]" />
							)}
							<div
								className={cn(
									"text-[10px] uppercase font-black tracking-widest",
									isToday ? "text-indigo-400" : "text-slate-500",
								)}
							>
								{day.toLocaleDateString("pt-BR", { weekday: "short" })}
							</div>
							<div
								className={cn(
									"text-lg font-black",
									isToday ? "text-white" : "text-slate-300",
								)}
							>
								{day.getDate()}
							</div>
						</div>
					);
				})}
			</div>

			<ScrollArea ref={scrollRef} className="flex-1 h-full">
				<div className="grid grid-cols-[80px_repeat(7,1fr)] min-w-[900px]">
					{hours.map((hour) => (
						<Fragment key={hour}>
							<div
								data-hour={hour}
								className={cn(
									"h-[210px] border-r border-b border-slate-800 flex justify-center pt-4 text-[11px] font-bold tabular-nums transition-colors",
									hour === currentHour
										? "bg-indigo-500/20 text-indigo-300 ring-inset ring-1 ring-indigo-500/30"
										: "bg-slate-900/30 text-slate-500",
								)}
							>
								{`${hour.toString().padStart(2, "0")}:00`}
							</div>

							{days.map((day) => {
								const isToday = day.toDateString() === todayStr;
								const isPastHour = new Date(day).setHours(hour) < now;
								const isCurrentHour = isToday && hour === currentHour;

								const appointments = cellAppointments(day, hour);

								return (
									<div
										key={`${day.toISOString()}-${hour}`}
										className={cn(
											"h-[210px] border-r border-b border-slate-800 last:border-r-0 p-2 transition-colors relative",
											isCurrentHour && "bg-indigo-500/10",
											!isCurrentHour && isToday && "bg-indigo-500/[0.02]",
											isPastHour &&
												"bg-black/30 grayscale-[0.3] pointer-events-none opacity-60",
										)}
									>
										<ScrollArea className="h-full w-full">
											<div className="flex flex-col gap-2 p-2">
												{appointments.map((a) => (
													<AppointmentCard key={a.id} appointment={a} />
												))}
											</div>
										</ScrollArea>
									</div>
								);
							})}
						</Fragment>
					))}
				</div>
				<ScrollBar orientation="horizontal" className="bg-slate-900" />
				<ScrollBar orientation="vertical" className="bg-slate-900" />
			</ScrollArea>
		</div>
	);
}
