import { Client as IClient } from "../entities/interfaces/Client";
import { Client } from "../entities/Client";
import UserSchema from '../dataAccess/schemas/User';
import { ClientRepository as IClientRepository } from "./interfaces/ClientRepository";
import { User } from "../entities/interfaces/User";
import { Types, Model, Document } from "mongoose";
import { default as ClientSchema } from "../dataAccess/schemas/Client"

export class ClientRepository implements IClientRepository {
    private model: Model<IClient>;

    constructor() {
        this.model = ClientSchema;
    }

    findById(id: string, user: User, callback: (error: any, result: Client) => void): void {
        this.model.findById(id, callback);
    }

    create(item: IClient, callback: (error: any, result: Client) => void): void {
        this.model.create(item, (error: any, result: IClient) => {
            callback(error, new Client(result));
        });
    }

    update(id: Types.ObjectId, item: IClient, callback: (error: any, result: Client) => void): void {
        this.model.update({ _id: id }, item, (error: any, result: IClient) => {
            callback(error, new Client(result));
        });
    }

    delete(id: string, user: User, callback: (error: any, result: any) => void): void {

    }
}