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
	CircleAlert,
	Columns2,
	EllipsisVertical,
	Search,
	X,
} from "lucide-react";
import React, { useEffect, useState } from "react";

import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import {
	Dialog,
	DialogClose,
	DialogContent,
	DialogDescription,
	DialogFooter,
	DialogHeader,
	DialogTitle,
} from "@/components/ui/dialog";
import {
	DropdownMenu,
	DropdownMenuCheckboxItem,
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
import { usePatients } from "@/lib/hooks/use-patients";
import { queryClient } from "@/lib/query-client";
import { maskCPF } from "@/utils/cpf-utils";
import { maskPhone } from "@/utils/phone-utils";
import { toast } from "sonner";

export function PatientsDataTable() {
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
	const [isArchiveDialogOpen, setIsArchiveDialogOpen] = useState(false);
	const [patientToArchive, setPatientToArchive] = useState(null);

	const { data: response, isLoading } = usePatients(
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

	const handleArchive = async () => {
		if (!patientToArchive) return;

		try {
			await api.delete(`/patients/${patientToArchive.id}`);

			queryClient.invalidateQueries(["patients"]);

			toast.success("Paciente arquivado com sucesso.");
		} catch (error) {
			console.error(error);

			toast.error("Erro ao arquivar paciente.");
		} finally {
			setIsArchiveDialogOpen(false);
			setPatientToArchive(null);
		}
	};

	const handleCancelArchive = () => {
		setIsArchiveDialogOpen(false);
		setPatientToArchive(null);
	};

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
			accessorKey: "name",
			header: "Nome do Paciente",
			cell: ({ row }) => (
				<span className="font-medium">{row.original.name}</span>
			),
		},
		{
			accessorKey: "document",
			header: "Documento",
			cell: ({ row }) => <span>{maskCPF(row.original.document)}</span>,
		},
		{
			accessorKey: "phone",
			header: "Telefone",
			cell: ({ row }) => <span>{maskPhone(row.original.phone)}</span>,
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
							onClick={() => navigate({ to: `/patients/${row.original.id}` })}
						>
							Ver Detalhes
						</DropdownMenuItem>
						<DropdownMenuItem
							onClick={() =>
								navigate({ to: `/patients/${row.original.id}/edit` })
							}
						>
							Editar Paciente
						</DropdownMenuItem>
						<DropdownMenuSeparator />
						<DropdownMenuItem
							className="text-destructive"
							onClick={() => {
								setPatientToArchive(row.original);
								setIsArchiveDialogOpen(true);
							}}
						>
							Arquivar
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

	return (
		<div className="flex flex-col gap-4 py-4">
			<div className="flex items-center justify-between px-4 lg:px-6">
				<div className="flex items-center gap-4 flex-1">
					<h2 className="text-xl font-semibold hidden sm:block">Pacientes</h2>
					<div className="relative w-full max-w-sm">
						<Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
						<Input
							placeholder="Filtrar pacientes..."
							value={globalFilter ?? ""}
							onChange={(e) => setGlobalFilter(e.target.value)}
							className="pl-9 h-9"
						/>
					</div>
				</div>

				<div className="flex items-center gap-2">
					<DropdownMenu>
						<DropdownMenuTrigger asChild>
							<Button variant="outline" size="sm">
								<Columns2 className="size-4 mr-2" />
								<span className="hidden md:inline">Colunas</span>
								<ChevronDown className="size-4 ml-2" />
							</Button>
						</DropdownMenuTrigger>
						<DropdownMenuContent align="end" className="w-56">
							{table
								.getAllColumns()
								.filter((col) => col.getCanHide())
								.map((column) => (
									<DropdownMenuCheckboxItem
										key={column.id}
										className="capitalize"
										checked={column.getIsVisible()}
										onCheckedChange={(value) =>
											column.toggleVisibility(!!value)
										}
									>
										{column.id}
									</DropdownMenuCheckboxItem>
								))}
						</DropdownMenuContent>
					</DropdownMenu>
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
									Carregando pacientes...
								</TableCell>
							</TableRow>
						) : table.getRowModel().rows?.length ? (
							table.getRowModel().rows.map((row) => (
								<TableRow
									key={row.id}
									data-state={row.getIsSelected() && "selected"}
								>
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
									Nenhum paciente encontrado.
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

			<Dialog open={isArchiveDialogOpen} onOpenChange={setIsArchiveDialogOpen}>
				<DialogContent>
					<DialogHeader>
						<DialogTitle className="text-destructive flex flex-row items-center">
							<CircleAlert className="mr-2" />
							Arquivar paciente permanentemente?
						</DialogTitle>

						<DialogDescription className="text-foreground/90 space-y-2">
							<p>
								Você está prestes a <b>arquivar permanentemente</b> o paciente{" "}
								<b className="text-foreground">{patientToArchive?.name}</b>.
							</p>

							<p className="font-medium">
								Esta ação{" "}
								<span className="text-destructive font-bold">
									NÃO pode ser desfeita
								</span>
								.
							</p>

							<p className="text-sm opacity-90">Após o arquivamento:</p>
							<ul className="list-disc ml-5 text-sm opacity-90">
								<li>
									O paciente não aparecerá mais em buscas ou listas ativas;
								</li>
								<li>Atendimentos futuros não poderão ser registrados;</li>
								<li>O registro ficará inacessível para edição.</li>
							</ul>
						</DialogDescription>
					</DialogHeader>

					<DialogFooter className="mt-4 gap-2">
						<Button
							className="ring-0 bg-white hover:bg-white/70 focus:ring-0 focus:bg-white text-black"
							variant="secondary"
							onClick={handleCancelArchive}
						>
							Cancelar
						</Button>
						<Button onClick={handleArchive} variant="ghost" className=" ring-0">
							Confirmar arquivamento permanente
						</Button>
					</DialogFooter>
				</DialogContent>
			</Dialog>
		</div>
	);
}
