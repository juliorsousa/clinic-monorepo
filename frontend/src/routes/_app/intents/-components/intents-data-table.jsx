import {
	flexRender,
	getCoreRowModel,
	useReactTable,
} from "@tanstack/react-table";
import {
	ChevronDown,
	ChevronLeft,
	ChevronRight,
	Columns2,
	EllipsisVertical,
	Search,
} from "lucide-react";
import React, { useEffect, useState } from "react";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
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
	intentStatusClassNamesMap,
	intentStatusLabelMap,
	profileTypeMap,
} from "@/lib/constants";
import { useProfileIntents } from "@/lib/hooks/use-profile-intents";
import { useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { IntentDetailsDialog } from "./intent-details-dialog";
import { IntentRefuseDialog } from "./intent-refuse-dialog";

export function IntentsDataTable() {
	const queryClient = useQueryClient();
	const [data, setData] = useState([]);
	const [globalFilter, setGlobalFilter] = useState("");
	const [pagination, setPagination] = useState({
		pageIndex: 0,
		pageSize: 10,
		totalPages: 0,
		totalElements: 0,
	});

	const [selectedIntent, setSelectedIntent] = useState(null);
	const [isDetailsOpen, setIsDetailsOpen] = useState(false);
	const [isRefuseOpen, setIsRefuseOpen] = useState(false);

	const { data: response, isLoading } = useProfileIntents(
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

	const handleRefuse = async () => {
		try {
			await api.post(`/profiling/profile-intent/${selectedIntent.id}/reject`);

			queryClient.invalidateQueries(["profile-intents"]);
			toast.success("Solicitação recusada.");

			setIsRefuseOpen(false);
		} catch (error) {
			toast.error("Erro ao recusar solicitação.");
		}
	};

	const handleApprove = async () => {
		try {
			await api.post(`/profiling/profile-intent/${selectedIntent.id}/approve`);

			queryClient.invalidateQueries(["profile-intents"]);

			toast.success("Solicitação aprovada.");
			setIsDetailsOpen(false);
		} catch (error) {
			toast.error("Erro ao aprovar solicitação.");
		}
	};

	const columns = [
		{
			accessorKey: "type",
			header: "Tipo",
			cell: ({ row }) => (
				<Badge variant="outline">{profileTypeMap[row.getValue("type")]}</Badge>
			),
		},
		{
			id: "name",
			header: "Nome",
			cell: ({ row }) => {
				const body = JSON.parse(row.original.body);
				return <span>{body.personal?.personal?.name || "N/A"}</span>;
			},
		},
		{
			accessorKey: "status",
			header: "Status",
			cell: ({ row }) => {
				const status = row.getValue("status");

				return (
					<Badge className={intentStatusClassNamesMap[status]}>
						{intentStatusLabelMap[status]}
					</Badge>
				);
			},
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
							onClick={() => {
								setSelectedIntent(row.original);
								setIsDetailsOpen(true);
							}}
						>
							Visualizar
						</DropdownMenuItem>
						<DropdownMenuSeparator />
						<DropdownMenuItem
							className="text-destructive"
							disabled={row.original.status !== "PENDING"}
							onClick={() => {
								setSelectedIntent(row.original);
								setIsRefuseOpen(true);
							}}
						>
							Rejeitar
						</DropdownMenuItem>
					</DropdownMenuContent>
				</DropdownMenu>
			),
		},
	];

	const table = useReactTable({
		data,
		columns,
		manualPagination: true,
		pageCount: pagination.totalPages,
		state: {
			pagination: {
				pageIndex: pagination.pageIndex,
				pageSize: pagination.pageSize,
			},
		},
		onPaginationChange: (updater) => {
			if (typeof updater === "function") {
				const next = updater({
					pageIndex: pagination.pageIndex,
					pageSize: pagination.pageSize,
				});
				setPagination((prev) => ({ ...prev, ...next }));
			}
		},
		getCoreRowModel: getCoreRowModel(),
	});

	const intentData = selectedIntent ? JSON.parse(selectedIntent.body) : null;
	const errorData = selectedIntent?.response
		? JSON.parse(selectedIntent.response)
		: null;

	const isReprofilingIntent =
		intentData?.personal?.personal === null ||
		intentData?.personal?.personal === undefined;

	return (
		<div className="flex flex-col gap-4 py-4">
			<div className="flex items-center justify-between px-4 lg:px-6">
				<div className="flex items-center gap-4 flex-1">
					<h2 className="text-xl font-semibold hidden sm:block">
						Solicitações
					</h2>
					<div className="relative w-full max-w-sm">
						<Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
						<Input
							placeholder="Filtrar solicitações..."
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
									Carregando solicitações...
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
									Nenhuma solicitação encontrado.
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

			<IntentDetailsDialog
				isOpen={isDetailsOpen}
				onOpenChange={setIsDetailsOpen}
				intentData={intentData}
				handleApprove={handleApprove}
				selectedIntent={selectedIntent}
				errorData={errorData}
				isReprofilingIntent={isReprofilingIntent}
			/>

			<IntentRefuseDialog
				isOpen={isRefuseOpen}
				onOpenChange={setIsRefuseOpen}
				onConfirm={handleRefuse}
			/>
		</div>
	);
}
