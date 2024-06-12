import { useSpinDelay } from "spin-delay";
import { Icon } from "./ui/icon";

export default function Spinner({ loading }: { loading: boolean }) {
  const showSpinner = useSpinDelay(loading, { delay: 500, minDuration: 200 });

  if (showSpinner) {
    return <Icon name="update" size="xl" className="animate-spin" />;
  }
}
