import {
	flexRender,
	getCoreRowModel,
	getFilteredRowModel,
	getPaginationRowModel,
	getSortedRowModel,
	useReactTable,
} from "@tanstack/react-table";
import React, { useCallback, useEffect, useMemo, useState } from "react";

import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
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
import {
	ChevronDown,
	ChevronLeft,
	ChevronRight,
	Columns2,
	EllipsisVertical,
	Plus,
	Search,
} from "lucide-react";

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
					onCheckedChange={(value) => table.toggleAllPageRowsSelected(!!value)}
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
		cell: ({ row }) => <span className="font-medium">{row.original.name}</span>,
	},
	{
		accessorKey: "document",
		header: "Documento",
	},
	{
		accessorKey: "phone",
		header: "Telefone",
	},
	{
		id: "actions",
		cell: () => (
			<DropdownMenu>
				<DropdownMenuTrigger asChild>
					<Button
						variant="ghost"
						className="data-[state=open]:bg-muted text-muted-foreground flex size-8"
						size="icon"
					>
						<EllipsisVertical />
						<span className="sr-only">Abrir menu</span>
					</Button>
				</DropdownMenuTrigger>
				<DropdownMenuContent align="end" className="w-32">
					<DropdownMenuItem>Ver Detalhes</DropdownMenuItem>
					<DropdownMenuItem>Editar Paciente</DropdownMenuItem>
					<DropdownMenuSeparator />
					<DropdownMenuItem variant="destructive">Arquivar</DropdownMenuItem>
				</DropdownMenuContent>
			</DropdownMenu>
		),
	},
];

export function PatientsDataTable() {
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
	const [loading, setLoading] = useState(false);

	const fetchData = useCallback(async () => {
		setLoading(true);

		try {
			const response = await api.get("/patients/", {
				params: {
					page: pagination.pageIndex,
					size: pagination.pageSize,
				},
			});

			setData(response.data.content || []);
			setPagination((prev) => ({
				...prev,
				totalPages: response.data.totalPages,
				totalElements: response.data.totalElements,
			}));
		} catch (error) {
			console.error("Erro ao carregar pacientes:", error);
		} finally {
			setLoading(false);
		}
	}, [pagination.pageIndex, pagination.pageSize]);

	useEffect(() => {
		fetchData();
	}, [fetchData]);

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
		onPaginationChange: ({ pageIndex, pageSize }) =>
			setPagination((prev) => ({ ...prev, pageIndex, pageSize })),
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
					<Button variant="default" size="sm">
						<Plus className="size-4 mr-2" />
						<span>Adicionar Paciente</span>
					</Button>
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
						{loading ? (
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

			<div className="flex items-center justify-between px-4 lg:px-10">
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
									pageIndex: Math.max(prev.pageIndex - 1, 0),
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
									pageIndex: Math.min(prev.pageIndex + 1, prev.totalPages - 1),
								}))
							}
							disabled={pagination.pageIndex + 1 >= pagination.totalPages}
						>
							<ChevronRight className="size-4" />
						</Button>
					</div>
				</div>
			</div>
		</div>
	);
}
