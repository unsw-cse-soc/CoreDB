export interface Read<T> {
    retrieve: (callback: (error: any, result: Array<T>) => void) => void;
    findById: (id: string, callback: (error: any, result: T) => void) => void;
}