import { Document } from "mongoose";

export interface Client extends Document {
    name: string;
    secret: string;
    refreshTokenLifeTime: number;
}