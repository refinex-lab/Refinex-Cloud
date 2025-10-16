<script setup lang="ts">
defineOptions({
  name: 'GlobalFooter'
});

const currentYear = new Date().getFullYear();
</script>

<template>
  <DarkModeContainer class="h-full flex-center bg-transparent">
    <span class="copyright-text">
      Copyright © {{ currentYear }} Refinex. All rights reserved.
    </span>
  </DarkModeContainer>
</template>

<style scoped>
.copyright-text {
  color: var(--n-text-color-2);
  font-size: 12px;
  text-align: center;
}
</style>
