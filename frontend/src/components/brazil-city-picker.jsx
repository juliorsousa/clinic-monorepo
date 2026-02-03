import { brazilCitiesPerState, getCitiesByState } from "@/utils/cities-states";
import {
	FormControl,
	FormField,
	FormItem,
	FormLabel,
	FormMessage,
} from "./ui/form";

import {
	Select,
	SelectContent,
	SelectGroup,
	SelectItem,
	SelectLabel,
	SelectTrigger,
	SelectValue,
} from "./ui/select";

export default function CityPicker({ value, state, onChange }) {
	const cities = getCitiesByState(state);

	return (
		<Select value={value} onValueChange={onChange} disabled={!state}>
			<SelectTrigger className="w-60 lg:w-83">
				<SelectValue
					placeholder={
						state ? "Selecione uma cidade" : "Selecione um estado primeiro"
					}
				/>
			</SelectTrigger>

			<SelectContent>
				<SelectGroup>
					<SelectLabel>Cidades</SelectLabel>

					{cities.map(([city]) => (
						<SelectItem key={city} value={city}>
							{city}
						</SelectItem>
					))}
				</SelectGroup>
			</SelectContent>
		</Select>
	);
}
