import { refreshServer } from "../../store/store";

export const refresh = () => {
	refreshServer.update(v => !v);
}