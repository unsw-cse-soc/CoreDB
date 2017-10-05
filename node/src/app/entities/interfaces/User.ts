import { Document } from "mongoose";
import { Client } from "./Client";

export interface User extends Document {
    client: Client;
    name: string;
    password: string;
    username: string;
}