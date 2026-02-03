import { Button } from "@/components/ui/button";
import {
	Dialog,
	DialogContent,
	DialogDescription,
	DialogFooter,
	DialogHeader,
	DialogTitle,
} from "@/components/ui/dialog";
import { CircleAlert } from "lucide-react";
import React from "react";

export function CancelAppointmentDialog({ isOpen, onOpenChange, onConfirm }) {
	return (
		<Dialog open={isOpen} onOpenChange={onOpenChange}>
			<DialogContent>
				<DialogHeader>
					<DialogTitle className="text-destructive flex items-center gap-2">
						<CircleAlert className="h-5 w-5" />
						Cancelar a Consulta?
					</DialogTitle>
					<DialogDescription>
						Isso marcará a consulta como cancelada. Esta ação não pode ser
						desfeita.
					</DialogDescription>
				</DialogHeader>
				<DialogFooter>
					<Button variant="outline" onClick={() => onOpenChange(false)}>
						Voltar
					</Button>
					<Button variant="destructive" onClick={onConfirm}>
						Confirmar Cancelamento
					</Button>
				</DialogFooter>
			</DialogContent>
		</Dialog>
	);
}
