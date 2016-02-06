package ua.eshepelyuk.ratpack

import com.google.inject.name.Named
import ratpack.error.ServerErrorHandler
import ratpack.handling.Context

import javax.inject.Inject
import javax.validation.ConstraintViolationException

import static io.netty.handler.codec.http.HttpResponseStatus.UNPROCESSABLE_ENTITY
import static ratpack.http.Status.of

class MyServerErrorHandler implements ServerErrorHandler {

    @Inject
    @Named("defaultServerErrorHandler")
    ServerErrorHandler delegateErrorHandler

    @Override
    void error(Context context, Throwable throwable) throws Exception {
        if (throwable instanceof ConstraintViolationException) {
            context.response.status(of(UNPROCESSABLE_ENTITY.code(), "Entity validation failed")).send()
        } else {
            delegateErrorHandler.error(context, throwable)
        }
    }
}
