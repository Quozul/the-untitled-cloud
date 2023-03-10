export type ApiServer = {
	version: string;
	eula: boolean;
	serverType: string;
	useAikar: boolean;
	jvmFlags: string | null;
	memory: string;
	forgeVersion: string | null;
	fabricLauncherVersion: string | null;
	fabricLoaderVersion: string | null;
	quiltLauncherVersion: string | null;
	quiltLoaderVersion: string | null;
	ftbModpackId: number | null;
	ftbModpackVersionId: number | null;
};
