import { Request, Response } from "express";
import { post } from "request";
import { ClientRepository } from "../app/repository/ClientRepository";
import { Client } from "../app/entities/interfaces/Client";

export default class ClientController {
    create(req: Request, res: Response) {
        try {
            var model: Client = req.body as Client;
            var clientRepository: ClientRepository = new ClientRepository();
            clientRepository.create(model, (error, result) => {
                if (error) {
                    res.send({ "error": "error in your request" });
                } else {
                    res.send(result);
                }
            })
        } catch (error) {
            console.log(error);
            res.send({ "error": "error in your request" });
        }
    }
}