import { Types } from "mongoose";

export interface IWrite<T> {
    create: (item: T, callback: (error: any, result: any) => void) => void;
    update: (id: Types.ObjectId, item: T, callback: (error: any, result: any) => void) => void;
    delete: (id: string, callback: (error: any, result: any) => void) => void;
}