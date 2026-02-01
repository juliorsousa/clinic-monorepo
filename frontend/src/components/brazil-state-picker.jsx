import { brazilStates } from "@/utils/cities-states";

import {
	Select,
	SelectContent,
	SelectGroup,
	SelectItem,
	SelectLabel,
	SelectTrigger,
	SelectValue,
} from "./ui/select";

export default function StatePicker({ value, onChange, disabled = false }) {
	return (
		<Select onValueChange={onChange} value={value} disabled={disabled}>
			<SelectTrigger className="w-20">
				<SelectValue placeholder="-" />
			</SelectTrigger>

			<SelectContent>
				<SelectGroup>
					<SelectLabel>Estados do Brasil</SelectLabel>

					{brazilStates.map(([uf]) => (
						<SelectItem key={uf} value={uf}>
							{uf}
						</SelectItem>
					))}
				</SelectGroup>
			</SelectContent>
		</Select>
	);
}
