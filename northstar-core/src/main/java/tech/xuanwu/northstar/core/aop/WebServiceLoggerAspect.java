package tech.xuanwu.northstar.core.aop;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * webservice日志切面
 * @author kevinhuangwl
 *
 */
@Slf4j
@Aspect
@Component
public class WebServiceLoggerAspect {

	@Pointcut("@annotation(io.swagger.annotations.ApiOperation)")
	public void allWebServiceMethod() {}
	
	@Before("allWebServiceMethod()")
	public void incomingRequestLogger(JoinPoint joinPoint) {
		//获取RequestAttributes  
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();  
        //从获取RequestAttributes中获取HttpServletRequest的信息  
        HttpServletRequest req = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        
        
		Signature sign = joinPoint.getSignature();
		String name = sign.getName();
		
		log.info("【日志审计】 监控接口 [ {} ] 被IP：{} 调用", name, req.getRemoteAddr());
		
	}
}
