import { Request, Response } from "express";
import { BaseController } from "./interfaces/base/BaseController";
import { User as IUser } from "../app/entities/interfaces/User";

export default class UserController implements IBaseController<UserBusiness> {

    create(req: Request, res: Response): void {
        try {
            var user: IUser = <IUser>req.body;
            var userBusiness = new UserBusiness();
            userBusiness.create(user, (error, result) => {
                if (error) {
                    if (error.code && error.code === 11000) {
                        res.send(400, { "error": "This is user name is taken." });
                    } else {
                        res.send(400, { "error": "error in your request" });
                    }
                }
                else res.send(result);
            });
        }
        catch (e) {
            console.log(e);
            res.send({ "error": "error in your request" });
        }
    }

    update(req: Request, res: Response): void {
        try {
            var user: IUser = <IUser>req.body;
            var _id: string = req.params._id;
            var userBusiness = new UserBusiness();
            userBusiness.update(_id, user, (error, result) => {
                if (error) res.send({ "error": "error" });
                else res.send({ "success": "success" });
            });
        }
        catch (e) {
            console.log(e);
            res.send({ "error": "error in your request" });
        }
    }

    delete(req: Request, res: Response): void {
        try {
            var _id: string = req.params._id;
            var userBusiness = new UserBusiness();
            userBusiness.delete(_id, (error, result) => {
                if (error) res.send({ "error": "error" });
                else res.send({ "success": "success" });
            });
        }
        catch (e) {
            console.log(e);
            res.send({ "error": "error in your request" });
        }
    }
    retrieve(req: Request, res: Response): void {
        res.sendStatus(401);
        // try {
        //     var userBusiness = new UserBusiness();
        //     userBusiness.retrieve((error, result) => {
        //         if (error) res.send({ "error": "error" });
        //         else res.send(result);
        //     });
        // }
        // catch (e) {
        //     console.log(e);
        //     res.send({ "error": "error in your request" });
        // }
    }
    findById(req: Request, res: Response): void {
        res.sendStatus(401);
        // try {
        //     var _id: string = req.params._id;
        //     var userBusiness = new UserBusiness();
        //     userBusiness.findById(_id, (error, result) => {
        //         if (error) res.send({ "error": "error" });
        //         else res.send(result);
        //     });
        // }
        // catch (e) {
        //     console.log(e);
        //     res.send({ "error": "error in your request" });
        // }
    }
}  