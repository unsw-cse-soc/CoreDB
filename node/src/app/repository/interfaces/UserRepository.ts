import { User } from "../../entities/interfaces/User";
import { Client as IClient } from "../../entities/interfaces/Client";
import { Client } from "../../entities/Client";
import { Types } from "mongoose";

export interface ClientRepository {
    findById: (id: string, user: User, callback: (error: any, result: Client) => void) => void;
    create: (item: IClient, callback: (error: any, result: Client) => void) => void;
    update: (id: Types.ObjectId, item: IClient, callback: (error: any, result: Client) => void) => void;
    delete: (id: string, user: User, callback: (error: any, result: any) => void) => void;
}

