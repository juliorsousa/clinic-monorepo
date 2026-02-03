import { medicalSpecialties } from "@/utils/specialties";
import {
	Select,
	SelectContent,
	SelectGroup,
	SelectItem,
	SelectLabel,
	SelectTrigger,
	SelectValue,
} from "./ui/select";

export default function SpecialtyPicker({ value, onChange, disabled = false }) {
	return (
		<Select onValueChange={onChange} value={value} disabled={disabled}>
			<SelectTrigger className="w-full">
				<SelectValue placeholder="Selecione a especialidade" />
			</SelectTrigger>

			<SelectContent>
				<SelectGroup>
					<SelectLabel>Especialidades Médicas</SelectLabel>
					{medicalSpecialties.map(([key, label]) => (
						<SelectItem key={key} value={key}>
							{label}
						</SelectItem>
					))}
				</SelectGroup>
			</SelectContent>
		</Select>
	);
}
