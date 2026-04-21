<template>
  <footer class="bg-white text-black pt-5 pb-3 border-top border-1 border-secondary">
    <div class="container">
      <div class="row g-4">
        <div class="col-md-4">
          <router-link to="/" class="navbar-brand d-flex align-items-center gap-2">
            <img src="/logo/iKnowVation.png" alt="KnowVation" style="height: 28px" />
          </router-link>
          <p class="small text-muted font-monospace">Learn, Know, Grow, Success</p>
        </div>
        <div class="col-md-2">
          <h5>關於我們</h5>
          <ul class="list-unstyled">
            <li>
              <router-link to="/" class="text-black text-decoration-none hover-underline"
                >網站地圖</router-link
              >
            </li>
            <li class="text-black text-decoration-none">隱私政策</li>
          </ul>
        </div>
        <div class="col-md-2">
          <h5>關注我們</h5>
          <ul class="list-unstyled">
            <a
              href="https://github.com/xzhen719/online_course_platform_project"
              target="_blank"
              style="color: inherit; text-decoration: none; outline: none"
              >Github</a
            >
            <li>Facebook</li>
          </ul>
        </div>
        <div class="col-md-4">
          <h5>訂閱電子報</h5>
          <div class="input-group">
            <input type="email" class="form-control rounded-0" placeholder="您的Email..." />
            <button class="btn btn-success">訂閱</button>
          </div>
        </div>
      </div>
      <div class="text-start mt-5 pt-3">
        <small class="text-muted">&copy; 2025 iKnowVation. All rights reserved.</small>
      </div>
    </div>
  </footer>
</template>
