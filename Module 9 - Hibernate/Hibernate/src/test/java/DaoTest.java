import org.example.Book;
import org.example.Dao;
import org.example.Student;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

class DaoTest {
    static Dao<Book, Long> bookDao;
    static Dao<Student, Long> studentDao;

    @BeforeAll
    static void setUp() throws Exception {
        bookDao = new Dao<Book, Long>();
        // normally we would configure a different database for testing
        bookDao.setup("hibernate.cfg.xml");

        studentDao = new Dao<Student, Long>();
        studentDao.setup("hibernate.cfg.xml");
    }

    @AfterAll
    static void tearDown() {
        bookDao.clear("Book");
        bookDao.exit();

        studentDao.clear("Student");
        studentDao.exit();
    }


    //    They should have another Nested set of tests
    @Nested
    class BookTests {
        Book book;

        @BeforeEach
        void setUp() {
            book = new Book();
            book.setTitle("Title");
            book.setAuthor("Author");
            book.setPrice(0.0f);
        }

        @Test
        void create() {
            bookDao.create(book);
            Book bookFromDB = bookDao.read(Book.class, book.getBookId());
            assertEquals(book.getTitle(), bookFromDB.getTitle());
        }

        @Test
        void update() {
            bookDao.create(book);
            book.setTitle("Updated Title");
            bookDao.update(book);
            Book bookFromDB = bookDao.read(Book.class, book.getBookId());
            assertEquals(book.getTitle(), bookFromDB.getTitle());
        }

        @Test
        void delete() throws Exception {
            bookDao.create(book);
            Book bookFromDB = bookDao.read(Book.class, book.getBookId());
            if (bookFromDB != null){
                bookDao.delete(book);
                bookFromDB = bookDao.read(Book.class, book.getBookId());
                assertNull(bookFromDB);
            }
            else {
                throw new Exception("Create Doesn't Work");
            }
        }
    }

    @Nested
    class StudentTests {
        Student student;

        @BeforeEach
        void setUp() {
            student = new Student();
            student.setFirstName("Ammon");
            student.setLastName("Gleason");
            student.setAge(23);
        }

        @Test
        void create() {
            studentDao.create(student);
            Student studentFromDB = studentDao.read(Student.class, student.getStudentId());
            assertEquals(student.getFirstName(), studentFromDB.getFirstName());
        }

        @Test
        void update() {
            studentDao.create(student);
            student.setFirstName("Updated first name");
            studentDao.update(student);
            Student studentFromDB = studentDao.read(Student.class, student.getStudentId());
            assertEquals(student.getFirstName(), studentFromDB.getFirstName());
        }

        @Test
        void delete() throws Exception {
            studentDao.create(student);
            Student studentFromDB = studentDao.read(Student.class, student.getStudentId());
            if (studentFromDB != null){
                studentDao.delete(student);
                studentFromDB = studentDao.read(Student.class, student.getStudentId());
                assertNull(studentFromDB);
            }
            else {
                throw new Exception("Create Doesn't Work");
            }
        }
    }
}