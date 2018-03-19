

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

/**
 * Servlet Filter implementation class ResponseTime
 */
@WebFilter("/TimingFilter")
public class TimingFilter implements Filter {
	protected FilterConfig config;
    /**
     * Default constructor. 
     */
    public TimingFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here
		long startTime = System.nanoTime();
	    chain.doFilter(request, response);
	    long endTime = System.nanoTime();
	    long elapsedTime = endTime - startTime;
//	    String name = "Search";
//	    if (request instanceof HttpServletRequest) {
//	      name = ((HttpServletRequest) request).getRequestURI();
//	    }

	    config.getServletContext().log("TS " + elapsedTime);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
		this.config = fConfig;
	}

}
