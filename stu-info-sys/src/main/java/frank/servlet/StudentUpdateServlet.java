package frank.servlet;

import frank.dao.StudentDAO;
import frank.model.Student;
import frank.util.JSONUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/student/update")
public class StudentUpdateServlet extends AbstractBaseServlet {
    @Override
    protected Object process(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Student s = JSONUtil.read(req.getInputStream(), Student.class);
        StudentDAO.update(s);
        return null;
    }
}
