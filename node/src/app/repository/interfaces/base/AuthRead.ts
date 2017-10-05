import { User } from "../../../entities/interfaces/User";

export interface IAuthRead<T> {
    retrieve: (user: User, callback: (error: any, result: Array<T>) => void) => void;
    findById: (id: string, user: User, callback: (error: any, result: T) => void) => void;
}