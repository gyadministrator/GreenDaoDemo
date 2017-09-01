package com.example.gy.greendaodemo;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gy.greendaodemo.dao.DaoMaster;
import com.example.gy.greendaodemo.dao.DaoSession;
import com.example.gy.greendaodemo.dao.UserDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import bean.User;

public class MainActivity extends AppCompatActivity {
    private UserDao userDao;
    private EditText etId;
    private EditText etName;
    private Button btnAdd;
    private Button btnDelete;
    private Button btnQuery;
    private Button btnUpdate;
    private Button btnAllQuery;
    private TextView tvQuery;
    private LinearLayout linear_page;
    private Button btn_ok;
    private Button btn_page;
    private Button btn_pre;
    private Button btn_next;
    private EditText page_edit;
    private int pageNum = 0;
    private int allPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initDbHelp();

        /*新增一条数据*/
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = etId.getText().toString();
                String name = etName.getText().toString();
                if (isNotEmpty(id) && isNotEmpty(name)) {
                    QueryBuilder qb = userDao.queryBuilder();
                    ArrayList<User> list = (ArrayList<User>) qb.where(UserDao.Properties.Id.eq(id)).list();
                    if (list.size() > 0) {
                        Toast.makeText(MainActivity.this, "主键重复", Toast.LENGTH_SHORT).show();
                    } else {
                        userDao.insert(new User(Long.valueOf(id), name));
                        Toast.makeText(MainActivity.this, "插入数据成功", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (isEmpty(id) && isNotEmpty(name)) {
                        Toast.makeText(MainActivity.this, "id为空", Toast.LENGTH_SHORT).show();
                    }
                    if (isEmpty(name) && isNotEmpty(id)) {
                        Toast.makeText(MainActivity.this, "姓名为空", Toast.LENGTH_SHORT).show();
                    }
                    if (isEmpty(id) && isEmpty(name)) {
                        Toast.makeText(MainActivity.this, "请填写信息", Toast.LENGTH_SHORT).show();
                    }

                }
                etId.setText("");
                etName.setText("");
            }
        });

        /*删除指定数据*/
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = etId.getText().toString();
                if (isNotEmpty(id)) {
                    userDao.deleteByKey(Long.valueOf(id));
                    QueryBuilder qb = userDao.queryBuilder();
                    ArrayList<User> list = (ArrayList<User>) qb.where(UserDao.Properties.Id.eq(id)).list();
                    if (list.size() < 1) {
                        Toast.makeText(MainActivity.this, "删除数据成功", Toast.LENGTH_SHORT).show();
                        etId.setText("");
                        etName.setText("");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "id为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*查询数据*/
        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = etId.getText().toString();
                if (isNotEmpty(id)) {
                    QueryBuilder qb = userDao.queryBuilder();
                    ArrayList<User> list = (ArrayList<User>) qb.where(UserDao.Properties.Id.eq(id)).list();
                    if (list.size() > 0) {
                        String text = "";
                        for (User user : list) {
                            text = text + "\r\n" + user.getName();
                        }
                        tvQuery.setText(text);
                    } else {
                        tvQuery.setText("");
                        Toast.makeText(MainActivity.this, "不存在该数据", Toast.LENGTH_SHORT).show();
                    }
                    etId.setText("");
                    etName.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "id为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*查询全部数据*/
        btnAllQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QueryBuilder qb = userDao.queryBuilder();
                ArrayList<User> list = (ArrayList<User>) qb.list();
                if (list.size() > 0) {
                    String text = "";
                    for (User user : list) {
                        text = text + "\r\n" + user.getName() + "  ";
                    }
                    tvQuery.setText(text);
                } else {
                    tvQuery.setText("");
                    Toast.makeText(MainActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                }
                etId.setText("");
                etName.setText("");
            }
        });
        /*更新数据*/
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = etId.getText().toString();
                String name = etName.getText().toString();
                if (isNotEmpty(id) && isNotEmpty(name)) {
                    QueryBuilder qb = userDao.queryBuilder();
                    ArrayList<User> list = (ArrayList<User>) qb.where(UserDao.Properties.Id.eq(id)).list();
                    if (list.size() > 0) {
                        userDao.update(new User(Long.valueOf(id), name));
                        Toast.makeText(MainActivity.this, "更新数据成功", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (isEmpty(id) && isNotEmpty(name)) {
                        Toast.makeText(MainActivity.this, "id为空", Toast.LENGTH_SHORT).show();
                    }
                    if (isEmpty(name) && isNotEmpty(id)) {
                        Toast.makeText(MainActivity.this, "姓名为空", Toast.LENGTH_SHORT).show();
                    }
                    if (isEmpty(id) && isEmpty(name)) {
                        Toast.makeText(MainActivity.this, "请填写信息", Toast.LENGTH_SHORT).show();
                    }

                }
                etId.setText("");
                etName.setText("");
            }
        });

        /*显示分页视图*/
        btn_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear_page.setVisibility(View.VISIBLE);
            }
        });
        /*确定*/
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //确定操作
                String page = page_edit.getText().toString();
                int pageSize = Integer.parseInt(page);
                if (isNotEmpty(page)) {
                    ArrayList<User> list = (ArrayList<User>) getUserByPageSize(pageNum, pageSize);
                    if (list.size() > 0) {
                        String text = "";
                        for (User user : list) {
                            text = text + "\r\n" + user.getName() + "  ";
                        }
                        tvQuery.setText(text);
                    } else {
                        tvQuery.setText("");
                        Toast.makeText(MainActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                    }
                    etId.setText("");
                    etName.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "请输入每页显示的条数", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*上一页*/
        btn_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("next", pageNum + "");
                if (pageNum > 0 || pageNum == 0) {
                    if (pageNum == 0) {
                        Toast.makeText(MainActivity.this, "已经第一页了,没有上一页了", Toast.LENGTH_SHORT).show();
                    } else {
                        String page = page_edit.getText().toString();
                        int pageSize = Integer.parseInt(page);
                        if (isNotEmpty(page)) {
                            ArrayList<User> list = (ArrayList<User>) getUserByPageSize(--pageNum, pageSize);
                            if (list.size() > 0) {
                                String text = "";
                                for (User user : list) {
                                    text = text + "\r\n" + user.getName() + "  ";
                                }
                                tvQuery.setText(text);
                            } else {
                                tvQuery.setText("");
                                Toast.makeText(MainActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                            }
                            etId.setText("");
                            etName.setText("");
                        } else {
                            Toast.makeText(MainActivity.this, "请输入每页显示的条数", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        /*下一页*/
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("next", pageNum + "");
                QueryBuilder qb = userDao.queryBuilder();
                ArrayList<User> list = (ArrayList<User>) qb.list();
                String page = page_edit.getText().toString();
                int pageSize = Integer.parseInt(page);
                //拿到数据集合的大小
                if (list.size() > 0) {
                    if (list.size() % pageSize == 0) {
                        allPage = list.size() / pageSize;
                    } else {
                        allPage = (list.size() / pageSize) + 1;
                    }
                }
                if (list.size() > 0) {
                    if (pageNum == allPage-1) {
                        Toast.makeText(MainActivity.this, "已经最后一页了,没有下一页了", Toast.LENGTH_SHORT).show();
                    } else {
                        if (isNotEmpty(page)) {
                            ArrayList<User> list1 = (ArrayList<User>) getUserByPageSize(++pageNum, pageSize);
                            if (list.size() > 0) {
                                String text = "";
                                for (User user : list1) {
                                    text = text + "\r\n" + user.getName() + "  ";
                                }
                                tvQuery.setText(text);
                            } else {
                                tvQuery.setText("");
                                Toast.makeText(MainActivity.this, "没有数据", Toast.LENGTH_SHORT).show();
                            }
                            etId.setText("");
                            etName.setText("");
                        } else {
                            Toast.makeText(MainActivity.this, "请输入每页显示的条数", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    /**
     * 分页查询数据
     *
     * @param offset
     * @return
     */
    public List<User> getUserByPageSize(int offset, int pageSize) {
        List<User> listMsg = userDao.queryBuilder()
                .offset(offset * pageSize).limit(pageSize).list();
        return listMsg;
    }

    /*初始化数据库相关*/
    private void initDbHelp() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "recluse-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        userDao = daoSession.getUserDao();
    }

    private void initView() {
        etId = (EditText) findViewById(R.id.etId);
        etName = (EditText) findViewById(R.id.etName);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnQuery = (Button) findViewById(R.id.btnQuery);
        tvQuery = (TextView) findViewById(R.id.tvQuery);
        btnAllQuery = (Button) findViewById(R.id.btnAllQuery);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        linear_page = (LinearLayout) findViewById(R.id.linear_page);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_page = (Button) findViewById(R.id.btnPage);
        page_edit = (EditText) findViewById(R.id.page_edit);
        btn_pre = (Button) findViewById(R.id.btn_pre);
        btn_next = (Button) findViewById(R.id.btn_next);
    }

    private boolean isNotEmpty(String s) {
        if (s != null && !s.equals("") || s.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isEmpty(String s) {
        if (isNotEmpty(s)) {
            return false;
        } else {
            return true;
        }
    }
}
