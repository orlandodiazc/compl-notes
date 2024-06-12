import { useDebounce } from "@/lib/misc";
import { ChangeEvent, useId } from "react";
import { Input } from "./ui/input";
import { Icon } from "./ui/icon";
import { Label } from "./ui/label";

export function SearchBar({
  autoFocus = false,
  handleChange,
  defaultValue = "",
  placeholder = "Search...",
}: {
  autoFocus?: boolean;
  handleChange: (term: string | undefined) => void;
  defaultValue?: string;
  placeholder?: string;
}) {
  const id = useId();
  const inputId = `search-${id}`;

  const handleInputChange = useDebounce(
    (event: ChangeEvent<HTMLInputElement>) => {
      handleChange(event.target.value || undefined);
    },
    400,
  );
  return (
    <div className="relative">
      <Label htmlFor={inputId} className="sr-only">
        Search
      </Label>
      <Input
        onChange={handleInputChange}
        id={inputId}
        className="pl-10"
        placeholder={placeholder}
        defaultValue={defaultValue}
        autoFocus={autoFocus}
      />
      <Icon
        name="search"
        className="absolute left-3 top-1/2 -translate-y-1/2"
      />
    </div>
  );
}
