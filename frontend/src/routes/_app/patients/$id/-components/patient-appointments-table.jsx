import { useNavigate } from "@tanstack/react-router";
import {
	flexRender,
	getCoreRowModel,
	getFilteredRowModel,
	getPaginationRowModel,
	getSortedRowModel,
	useReactTable,
} from "@tanstack/react-table";
import {
	ChevronDown,
	ChevronLeft,
	ChevronRight,
	Columns2,
	EllipsisVertical,
	Search,
	X,
} from "lucide-react";
import React, { useEffect, useState } from "react";

import { Loading } from "@/components/loading";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import {
	DropdownMenu,
	DropdownMenuContent,
	DropdownMenuItem,
	DropdownMenuSeparator,
	DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
	Select,
	SelectContent,
	SelectItem,
	SelectTrigger,
	SelectValue,
} from "@/components/ui/select";
import {
	Table,
	TableBody,
	TableCell,
	TableHead,
	TableHeader,
	TableRow,
} from "@/components/ui/table";
import { api } from "@/lib/api";
import {
	appointmentStatusIconMap,
	appointmentStatusLabelMap,
	specialtyClassNamesMap,
	specialtyLabelMap,
} from "@/lib/constants";
import { usePatientAppointments } from "@/lib/hooks/use-patient-appointments";
import { useSummarizedDoctor } from "@/lib/hooks/use-summarized-doctor";
import { queryClient } from "@/lib/query-client";
import { CancelAppointmentDialog } from "@/routes/_app/appointments/-components/cancel-appointment-dialog";
import { formatDate } from "@/utils/format-date";
import { toast } from "sonner";

export function PatientAppointmentsTable({ patientId }) {
	const navigate = useNavigate();

	const [data, setData] = useState([]);
	const [rowSelection, setRowSelection] = useState({});
	const [columnVisibility, setColumnVisibility] = useState({});
	const [globalFilter, setGlobalFilter] = useState("");
	const [sorting, setSorting] = useState([]);
	const [pagination, setPagination] = useState({
		pageIndex: 0,
		pageSize: 10,
		totalPages: 0,
		totalElements: 0,
	});

	const [isCancelDialogOpen, setIsCancelDialogOpen] = useState(false);
	const [appointmentToCancel, setAppointmentToCancel] = useState(null);

	const { data: response, isLoading } = usePatientAppointments(
		patientId,
		pagination.pageIndex,
		pagination.pageSize,
	);

	useEffect(() => {
		if (response) {
			setData(response.content || []);
			setPagination((prev) => ({
				...prev,
				totalPages: response.totalPages,
				totalElements: response.totalElements,
			}));
		}
	}, [response]);

	const columns = [
		{
			id: "select",
			header: ({ table }) => (
				<div className="flex items-center justify-center">
					<Checkbox
						checked={
							table.getIsAllPageRowsSelected() ||
							(table.getIsSomePageRowsSelected() && "indeterminate")
						}
						onCheckedChange={(value) =>
							table.toggleAllPageRowsSelected(!!value)
						}
						aria-label="Selecionar todos"
					/>
				</div>
			),
			cell: ({ row }) => (
				<div className="flex items-center justify-center">
					<Checkbox
						checked={row.getIsSelected()}
						onCheckedChange={(value) => row.toggleSelected(!!value)}
						aria-label="Selecionar linha"
					/>
				</div>
			),
			enableSorting: false,
			enableHiding: false,
		},
		{
			accessorKey: "specialty",
			header: "Especialidade",
			cell: ({ row }) => {
				const { data: doctor, isLoading } = useSummarizedDoctor(
					row.original.doctorId,
				);

				if (isLoading) {
					return <Loading />;
				}

				return (
					<Badge className={specialtyClassNamesMap[doctor?.specialty] ?? ""}>
						{specialtyLabelMap[doctor?.specialty] ?? "Desconhecida"}
					</Badge>
				);
			},
		},
		{
			accessorKey: "doctorId",
			header: "Médico",
			cell: ({ row }) => {
				const { data: doctor } = useSummarizedDoctor(row.original.doctorId);
				return <span>{doctor?.name || "Carregando..."}</span>;
			},
		},
		{
			accessorKey: "scheduledTo",
			header: "Início",
			cell: ({ row }) => {
				const date = new Date(row.original.scheduledTo);
				return <span>{formatDate(date)}</span>;
			},
		},
		{
			accessorKey: "endAt",
			header: "Fim",
			cell: ({ row }) => {
				const date = new Date(row.original.scheduledTo);
				date.setHours(date.getHours() + 1);

				return <span>{formatDate(date)}</span>;
			},
		},
		{
			accessorKey: "status",
			header: "Status",
			cell: ({ row }) => {
				const status = row.getValue("status");

				return (
					<Badge variant={"outline"} className="text-muted-foreground px-1.5">
						{appointmentStatusIconMap[status]}
						{appointmentStatusLabelMap[status]}
					</Badge>
				);
			},
		},
		{
			accessorKey: "observation",
			header: "Observação",
			cell: ({ row }) => <span>{row.original.observation}</span>,
		},
		{
			id: "actions",
			cell: ({ row }) => (
				<DropdownMenu>
					<DropdownMenuTrigger asChild>
						<Button variant="ghost" className="size-8 p-0" size="icon">
							<EllipsisVertical className="size-4" />
						</Button>
					</DropdownMenuTrigger>
					<DropdownMenuContent align="end" className="w-40">
						<DropdownMenuItem
							disabled={row.original.status !== "SCHEDULED"}
							className="text-destructive"
							onClick={() => {
								setAppointmentToCancel(row.original);
								setIsCancelDialogOpen(true);
							}}
						>
							Cancelar
						</DropdownMenuItem>
					</DropdownMenuContent>
				</DropdownMenu>
			),
		},
	];

	const table = useReactTable({
		data,
		columns,
		pageCount: pagination.totalPages,
		state: {
			sorting,
			columnVisibility,
			rowSelection,
			pagination: {
				pageIndex: pagination.pageIndex,
				pageSize: pagination.pageSize,
			},
			globalFilter,
		},
		onGlobalFilterChange: setGlobalFilter,
		getRowId: (row) => row.id,
		onRowSelectionChange: setRowSelection,
		onSortingChange: setSorting,
		onColumnVisibilityChange: setColumnVisibility,
		onPaginationChange: (updater) => {
			if (typeof updater === "function") {
				const nextState = updater({
					pageIndex: pagination.pageIndex,
					pageSize: pagination.pageSize,
				});
				setPagination((prev) => ({ ...prev, ...nextState }));
			}
		},
		manualPagination: true,
		getCoreRowModel: getCoreRowModel(),
		getFilteredRowModel: getFilteredRowModel(),
		getPaginationRowModel: getPaginationRowModel(),
		getSortedRowModel: getSortedRowModel(),
	});

	const handleConfirmCancel = async () => {
		if (appointmentToCancel?.status !== "SCHEDULED") {
			toast.error("A consulta não pode mais ser cancelada.");
			return;
		}

		try {
			await api.delete(`/appointments/${appointmentToCancel.id}`);

			queryClient.invalidateQueries(["patient-appointments"]);
			toast.success("Consulta cancelada com sucesso.");
			setIsCancelDialogOpen(false);
		} catch (error) {
			console.error("Erro ao cancelar consulta:", error);

			toast.error("Erro ao cancelar consulta.");
		}
	};

	return (
		<div className="flex flex-col gap-4 py-4">
			<div className="flex items-center justify-between px-4 lg:px-6">
				<div className="flex items-center gap-4 flex-1">
					<h2 className="text-xl font-semibold hidden sm:block">Consultas</h2>
					<div className="relative w-full max-w-sm">
						<Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
						<Input
							placeholder="Filtrar consultas..."
							value={globalFilter ?? ""}
							onChange={(e) => setGlobalFilter(e.target.value)}
							className="pl-9 h-9"
						/>
					</div>
				</div>
			</div>

			<div className="overflow-hidden rounded-lg border mx-4 lg:mx-6">
				<Table>
					<TableHeader className="bg-muted sticky top-0 z-10">
						{table.getHeaderGroups().map((headerGroup) => (
							<TableRow key={headerGroup.id}>
								{headerGroup.headers.map((header) => (
									<TableHead key={header.id}>
										{header.isPlaceholder
											? null
											: flexRender(
													header.column.columnDef.header,
													header.getContext(),
												)}
									</TableHead>
								))}
							</TableRow>
						))}
					</TableHeader>
					<TableBody>
						{isLoading ? (
							<TableRow>
								<TableCell
									colSpan={columns.length}
									className="h-24 text-center"
								>
									Carregando consultas...
								</TableCell>
							</TableRow>
						) : table.getRowModel().rows?.length ? (
							table.getRowModel().rows.map((row) => (
								<TableRow key={row.id}>
									{row.getVisibleCells().map((cell) => (
										<TableCell key={cell.id}>
											{flexRender(
												cell.column.columnDef.cell,
												cell.getContext(),
											)}
										</TableCell>
									))}
								</TableRow>
							))
						) : (
							<TableRow>
								<TableCell
									colSpan={columns.length}
									className="h-24 text-center"
								>
									Nenhuma consulta encontrada.
								</TableCell>
							</TableRow>
						)}
					</TableBody>
				</Table>
			</div>

			<div className="flex items-center justify-between px-4 lg:px-6">
				<div className="text-muted-foreground text-sm">
					{table.getFilteredSelectedRowModel().rows.length} de{" "}
					{pagination.totalElements} selecionados
				</div>
				<div className="flex items-center gap-6">
					<div className="flex items-center gap-2">
						<Label className="text-sm font-medium">Linhas</Label>
						<Select
							value={`${pagination.pageSize}`}
							onValueChange={(val) =>
								setPagination((prev) => ({
									...prev,
									pageSize: Number(val),
									pageIndex: 0,
								}))
							}
						>
							<SelectTrigger className="w-17 h-8">
								<SelectValue />
							</SelectTrigger>
							<SelectContent>
								{[10, 20, 30].map((size) => (
									<SelectItem key={size} value={`${size}`}>
										{size}
									</SelectItem>
								))}
							</SelectContent>
						</Select>
					</div>
					<div className="flex items-center gap-2">
						<Button
							variant="outline"
							size="icon"
							className="h-8 w-8"
							onClick={() =>
								setPagination((prev) => ({
									...prev,
									pageIndex: prev.pageIndex - 1,
								}))
							}
							disabled={pagination.pageIndex === 0}
						>
							<ChevronLeft className="size-4" />
						</Button>
						<span className="text-sm font-medium">
							{pagination.pageIndex + 1} / {pagination.totalPages}
						</span>
						<Button
							variant="outline"
							size="icon"
							className="h-8 w-8"
							onClick={() =>
								setPagination((prev) => ({
									...prev,
									pageIndex: prev.pageIndex + 1,
								}))
							}
							disabled={pagination.pageIndex + 1 >= pagination.totalPages}
						>
							<ChevronRight className="size-4" />
						</Button>
					</div>
				</div>
			</div>

			<CancelAppointmentDialog
				isOpen={isCancelDialogOpen}
				onOpenChange={(value) => setIsCancelDialogOpen(value)}
				onConfirm={handleConfirmCancel}
			/>
		</div>
	);
}
