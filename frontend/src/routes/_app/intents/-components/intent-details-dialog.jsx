import {
	Clock,
	FileText,
	Hash,
	IdCard,
	IdCardLanyard,
	Layers,
	MapPin,
	Phone,
	Stethoscope,
	Tag,
} from "lucide-react";
import React from "react";

import { Button } from "@/components/ui/button";
import {
	Dialog,
	DialogContent,
	DialogFooter,
	DialogHeader,
	DialogTitle,
} from "@/components/ui/dialog";
import { profileTypeMap, specialtyLabelMap } from "@/lib/constants";
import { maskCPF } from "@/utils/cpf-utils";
import { formatDate } from "@/utils/format-date";
import { maskPhone } from "@/utils/phone-utils";

export function IntentDetailsDialog({
	isOpen,
	onOpenChange,
	intentData,
	selectedIntent,
	errorData,
	isReprofilingIntent,
	handleApprove,
}) {
	return (
		<Dialog open={isOpen} onOpenChange={onOpenChange}>
			<DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
				<DialogHeader>
					<DialogTitle>Detalhes da Solicitação</DialogTitle>
				</DialogHeader>

				{intentData && (
					<div className="flex flex-col gap-6 py-4">
						{/* Intent Info */}
						<section className="space-y-3">
							<h3 className="text-sm font-bold uppercase tracking-wider text-muted-foreground">
								Informações
							</h3>
							<div className="grid md:grid-cols-2 gap-4 border rounded-lg p-4 bg-card">
								<div className="flex items-center gap-3">
									<Tag className="h-5 w-5 text-primary" />
									<div className="flex flex-col">
										<span className="text-xs text-muted-foreground uppercase">
											Tipo
										</span>
										<span className="text-sm font-medium">
											{isReprofilingIntent ? "Reperfilação" : "Cadastro"}
										</span>
									</div>
								</div>
								<div className="flex items-center gap-3">
									<Clock className="h-5 w-5 text-primary" />
									<div className="flex flex-col">
										<span className="text-xs text-muted-foreground uppercase">
											Criada em
										</span>
										<span className="text-sm font-medium">
											{selectedIntent?.createdAt
												? formatDate(new Date(selectedIntent.createdAt))
												: "—"}
										</span>
									</div>
								</div>
							</div>
						</section>

						{/* Persona Info */}
						<section className="space-y-3">
							<h3 className="text-sm font-bold uppercase tracking-wider text-muted-foreground">
								Informações Pessoais
							</h3>
							<div className="grid md:grid-cols-2 gap-4 border rounded-lg p-4 bg-card">
								<div className="flex items-center gap-3">
									<IdCard className="h-5 w-5 text-primary" />
									<div className="flex flex-col">
										<span className="text-xs text-muted-foreground uppercase">
											Nome
										</span>
										<span className="text-sm font-medium">
											{intentData.personal?.personal?.name || "—"}
										</span>
									</div>
								</div>
								<div className="flex items-center gap-3">
									<Hash className="h-5 w-5 text-primary" />
									<div className="flex flex-col">
										<span className="text-xs text-muted-foreground uppercase">
											Documento
										</span>
										<span className="text-sm font-medium">
											{maskCPF(intentData.personal?.personal?.document || "") ||
												"—"}
										</span>
									</div>
								</div>
								<div className="flex items-center gap-3">
									<Phone className="h-5 w-5 text-primary" />
									<div className="flex flex-col">
										<span className="text-xs text-muted-foreground uppercase">
											Telefone
										</span>
										<span className="text-sm font-medium">
											{maskPhone(intentData.personal?.personal?.phone || "") ||
												"—"}
										</span>
									</div>
								</div>
								<div className="flex items-center gap-3">
									<FileText className="h-5 w-5 text-primary" />
									<div className="flex flex-col">
										<span className="text-xs text-muted-foreground uppercase">
											Tipo
										</span>
										<span className="text-sm font-medium">
											{profileTypeMap[selectedIntent.type]}
										</span>
									</div>
								</div>
							</div>
						</section>

						{/* Address Info */}
						{intentData.personal?.address && (
							<section className="space-y-3">
								<h3 className="text-sm font-bold uppercase tracking-wider text-muted-foreground">
									Endereço
								</h3>
								<div className="grid md:grid-cols-2 gap-4 border rounded-lg p-4 bg-card">
									<div className="flex items-center gap-3">
										<Layers className="h-5 w-5 text-primary" />
										<div className="flex flex-col">
											<span className="text-xs text-muted-foreground uppercase">
												Logradouro
											</span>
											<span className="text-sm font-medium">
												{intentData.personal.address.street},{" "}
												{intentData.personal.address.house}
											</span>
										</div>
									</div>
									<div className="flex items-center gap-3">
										<MapPin className="h-5 w-5 text-primary" />
										<div className="flex flex-col">
											<span className="text-xs text-muted-foreground uppercase">
												Cidade/Estado
											</span>
											<span className="text-sm font-medium">
												{intentData.personal.address.city} -{" "}
												{intentData.personal.address.state}
											</span>
										</div>
									</div>
								</div>
							</section>
						)}

						{/* Doctor Info */}
						{intentData.specific && intentData.profile?.value === "DOCTOR" && (
							<section className="space-y-3">
								<h3 className="text-sm font-bold uppercase tracking-wider text-muted-foreground">
									Informações Específicas
								</h3>
								<div className="grid md:grid-cols-2 gap-4 border rounded-lg p-4 bg-card">
									<div className="flex items-center gap-3">
										<IdCardLanyard className="h-5 w-5 text-primary" />
										<div className="flex flex-col">
											<span className="text-xs text-muted-foreground uppercase">
												Credencial
											</span>
											<span className="text-sm font-medium">
												{intentData.specific.credential}
											</span>
										</div>
									</div>
									<div className="flex items-center gap-3">
										<Stethoscope className="h-5 w-5 text-primary" />
										<div className="flex flex-col">
											<span className="text-xs text-muted-foreground uppercase">
												Especialidade
											</span>
											<span className="text-sm font-medium">
												{specialtyLabelMap[intentData.specific.specialty]}
											</span>
										</div>
									</div>
								</div>
							</section>
						)}

						{/* Error Info */}
						{selectedIntent.status === "ERRORED" && (
							<section className="space-y-3">
								<h3 className="text-sm font-bold uppercase tracking-wider text-destructive">
									Resposta de Erro
								</h3>
								<div className="p-4 bg-destructive/10 border border-destructive/20 rounded-lg text-destructive text-sm font-mono">
									{errorData?.message || "Erro desconhecido"}
								</div>
							</section>
						)}
					</div>
				)}
				{selectedIntent?.status === "PENDING" && (
					<DialogFooter className="justify-end">
						<Button
							className="bg-green-500 text-white hover:bg-green-700"
							onClick={handleApprove}
						>
							Aprovar
						</Button>
					</DialogFooter>
				)}
			</DialogContent>
		</Dialog>
	);
}
