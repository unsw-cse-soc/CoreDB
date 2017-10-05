import DataAccess from "../../dataAccess/DataAccess";
import { Schema } from "mongoose";
import { Client } from "../../entities/interfaces/Client";

var mongoose = DataAccess.mongooseInstance;
var mongooseConnection = DataAccess.mongooseConnection;

class ClientSchema {
    static get schema(): Schema {
        var schema: Schema = mongoose.Schema({
            name: {
                type: String,
                required: true,
                index: {
                    unique: true
                }
            },
            secret: {
                type: String,
                required: true,
                index: {
                    unique: true
                }
            },
            refreshTokenLifeTime: {
                type: Number,
                required: true
            }
        }, { timestamps: true });
        return schema;
    }
}
var schema = mongooseConnection.model<Client>("Clients", ClientSchema.schema);
export default schema;