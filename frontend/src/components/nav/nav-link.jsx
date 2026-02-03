import { Link, useLocation } from "@tanstack/react-router";
import React from "react";

export const NavLink = React.forwardRef((props, ref) => {
	const pathname = useLocation({
		select: (location) => location.pathname,
	});

	return (
		<Link
			className="flex items-center rounded-md p-2 font-medium text-muted-foreground text-sm data-[current=true]:bg-primary-foreground data-[current=true]:text-foreground"
			data-current={pathname === props.to}
			ref={ref}
			{...props}
		/>
	);
});

NavLink.displayName = "NavLink";
