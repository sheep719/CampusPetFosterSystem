<template>
  <div class="page-placeholder">
    <div class="placeholder-icon">🐕</div>
    <h2>寄养中宠物</h2>
    <p>该模块正在开发中，敬请期待~</p>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.page-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  color: #909399;
}

.placeholder-icon {
  font-size: 80px;
  margin-bottom: 20px;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

h2 {
  margin: 0 0 8px 0;
  font-size: 20px;
  color: #606266;
}

p {
  margin: 0;
  font-size: 14px;
}
</style>