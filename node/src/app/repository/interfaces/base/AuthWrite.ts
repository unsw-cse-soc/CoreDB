import { Types } from "mongoose";
import { User } from "../../../entities/interfaces/User";

export interface IAuthWrite<T> {
    create: (item: T, callback: (error: any, result: any) => void) => void;
    update: (id: Types.ObjectId, item: T, callback: (error: any, result: any) => void) => void;
    delete: (id: string, user: User, callback: (error: any, result: any) => void) => void;
}