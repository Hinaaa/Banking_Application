type MessageProps = {
    message:string,
    type: "Success" | "fail" | "error" | "info"
}
export default function ResponseMessage(props:Readonly<MessageProps>) {
    return(
        <>
            <div className={`response-message ${props.type}`}>
                {props.message}
            </div>
        </>
    )
}
