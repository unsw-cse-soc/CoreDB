import { RequestHandler } from "express";

export interface ReadController {
    retrieve: RequestHandler;
    findById: RequestHandler;
}