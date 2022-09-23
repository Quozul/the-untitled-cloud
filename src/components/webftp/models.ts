export enum FileType {
	DIRECTORY = "DIRECTORY",
	FILE = "FILE",
}

export type FileEntry = {
	size: number | null,
	time: number,
	name: string,
	file_type: FileType,
}
