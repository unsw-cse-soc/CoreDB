import { Client as IClient } from "./interfaces/Client";

export class Client {
    private _clientModel: IClient;

    constructor(clientModel: IClient) {
        this._clientModel = clientModel;
    }

    get name(): string {
        return this._clientModel.name;
    }

    get secret(): string {
        return this._clientModel.secret;
    }

    get refreshTokenLifeTime(): number {
        return this._clientModel.refreshTokenLifeTime;
    }
}