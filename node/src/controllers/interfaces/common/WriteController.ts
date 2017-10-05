import { RequestHandler } from "express";

export interface WriteController {
    create: RequestHandler;
    update: RequestHandler;
    delete: RequestHandler;
}