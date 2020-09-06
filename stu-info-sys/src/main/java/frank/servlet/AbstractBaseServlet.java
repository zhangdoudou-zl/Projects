package frank.servlet;

import frank.model.Response;
import frank.util.JSONUtil;
import frank.util.ThreadLocalHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class AbstractBaseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    //模板方法：参照HttpServlet的service方法和doXXX()方法的关系，service就是一个模板方法
    //模板方法是提供一种统一的处理逻辑，在不同条件调用不同的方法（子类重写的方法）：理解模板方法设计模式，java多态的理解
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //后边的接口的，Servlet中都是类似的写法，包括设置编码，数据类型，统一返回的数据格式
        //考虑几个问题：
        // 1.如果出现异常，应该怎么处理为好？
        // 2.怎么封装代码，进行统一设置编码，返回的数据格式，统一的处理异常更好？
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");

        PrintWriter pw = resp.getWriter();
        Response r = new Response();

        //HttpServletRequest对象.getParameter()方法，可以获取哪些请求数据？url和请求体，满足k1=v1&k2=v2格式的数据
        //表单提交的默认方式，表示的意思是请求的Content-Type字段x-www-form-urlencoded：
        //get会怎么样？在url中。post会怎么样？在请求体，格式为k1=v1&k2=v2
        //表单不使用默认的方式时，比如手写前端代码，发送ajax请求，请求格式为application/json：请求体，数据为json字符串
        //HttpServletRequest对象.getInputStream()通过输入流获取，请求体都可以获取到，怎么解析依赖自己代码实现
        try {
            Object o = process(req, resp);
            r.setSuccess(true);
            r.setCode("COK200");
            r.setMessage("操作成功");
            r.setTotal(ThreadLocalHolder.getTOTAL().get());//不管是否分页接口，都获取当前线程中的total变量
            r.setData(o);
        } catch (Exception e) {
            r.setCode("ERR500");
            r.setMessage(e.getMessage());
            StringWriter sw = new StringWriter();
            PrintWriter writer = new PrintWriter(sw);
            e.printStackTrace(writer);
            String stackTrace = sw.toString();
            System.err.println(stackTrace);
            r.setStackTrace(stackTrace);
        } finally {
            ThreadLocalHolder.getTOTAL().remove();//在线程结束前，一定要记得删除变量。如果不删除，可能存在内存泄漏的问题
        }
        pw.println(JSONUtil.write(r));
        pw.flush();
    }

    protected abstract Object process(HttpServletRequest req, HttpServletResponse resp) throws Exception;
}
