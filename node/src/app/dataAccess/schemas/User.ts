import DataAccess from "../../dataAccess/DataAccess";
import { Schema } from "mongoose";
import { User } from "../../entities/interfaces/User";
import { genSaltSync, hashSync } from "bcrypt";

var mongoose = DataAccess.mongooseInstance;
var mongooseConnection = DataAccess.mongooseConnection;

class UserSchema {
    static get schema(): Schema {
        var schema: Schema = mongoose.Schema({
            client: {
                type: mongoose.Schema.Types.ObjectId,
                ref: 'Clients',
                required: true
            },
            name: {
                type: String,
                required: true
            },
            password: {
                type: String,
                required: true
            },
            username: {
                type: String,
                required: true,
                index: {
                    unique: true
                }
            }
        }, { timestamps: true });
        schema.pre('save', function (next) {
            if (this._doc) {
                let doc = <User>this._doc;
                const salt = genSaltSync();
                doc.password = hashSync(doc.password, salt);
            }
            next();
        });
        schema.set('toJSON', {
            transform: (doc, ret, options) => {
                delete ret.password;
                delete ret.__v;
                return ret;
            }
        });
        return schema;
    }
}
var schema = mongooseConnection.model<User>("Users", UserSchema.schema);
export default schema;