type MessageProps = {
    message:string,
    messageType: "Success" | "error" | "info"
}
export default function ResponseMessage(props:Readonly<MessageProps>) {
    return(
        <>
            <div className={`response-message ${props.messageType}`}>
                {props.message}
            </div>
        </>
    )
}
