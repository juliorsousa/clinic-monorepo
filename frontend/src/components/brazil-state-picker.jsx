import { brazilStates } from "@/utils/cities-states";

import { useIsMobile } from "@/hooks/use-mobile";
import { cn } from "@/lib/utils";
import {
	Select,
	SelectContent,
	SelectGroup,
	SelectItem,
	SelectLabel,
	SelectTrigger,
	SelectValue,
} from "./ui/select";

export default function StatePicker({
	value,
	onChange,
	disabled = false,
	fullNames = false,
}) {
	const isMobile = useIsMobile();

	return (
		<Select onValueChange={onChange} value={value} disabled={disabled}>
			<SelectTrigger className={cn("w-20", fullNames && "w-60")}>
				<SelectValue placeholder="-" />
			</SelectTrigger>

			<SelectContent>
				<SelectGroup>
					<SelectLabel>Estados do Brasil</SelectLabel>

					{brazilStates.map(([uf, label]) => (
						<SelectItem key={uf} value={uf}>
							{fullNames ? (
								<>
									{label} <span className="text-muted-foreground">({uf})</span>
								</>
							) : (
								<>{uf}</>
							)}
						</SelectItem>
					))}
				</SelectGroup>
			</SelectContent>
		</Select>
	);
}
