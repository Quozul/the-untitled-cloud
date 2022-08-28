export type ApiUser = {
	email: string,
	communicationLanguage: string,
	discord: {
		id: string,
		username: string,
		discriminator: string,
		avatar: string | null,
	},
}